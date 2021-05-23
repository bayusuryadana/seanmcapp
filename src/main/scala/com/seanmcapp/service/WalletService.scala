package com.seanmcapp.service

import java.text.NumberFormat
import java.util.Calendar

import com.seanmcapp.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}

import scala.collection.SortedMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Seq[Wallet])

class WalletService(walletRepo: WalletRepo) {

  private val activeIncomeSet = Set("Salary", "Bonus")
  private val expenseSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Wellness", "Funding")

  private[service] val SECRET_KEY = WalletConf().secretKey

  def dashboard(implicit secretKey: String): DashboardView = {
    val wallets = authAndAwait(secretKey, walletRepo.getAll)
    val numberOfMonths = 6

    def sumAccount(account: String): Int = wallets.collect { case w if w.done && w.account == account => w.amount }.sum
    val sgd = sumAccount("DBS").formatNumber
    val idr = sumAccount("BCA").formatNumber
    val savingAccount = Map(
      "SGD" -> sgd,
      "IDR" -> idr
    )

    // pie data is based on SGD
    val adjWallet = adjustWallet(wallets)
    val pieMap = adjWallet.filter(w => w.done && expenseSet.contains(w.category)).groupBy(_.category).toSeq
      .map(cat => (cat._1, cat._2.map(-_.amount).sum))
    val totalIncome = adjWallet.filter(w => w.done && activeIncomeSet.contains(w.category)).map(_.amount).sum.toDouble
    val pie = pieMap.map(i => (i._1, (i._2 / totalIncome * 100).round2Digits())).unzip

    val monthsLabel = wallets.groupByDate().keys.takeRight(numberOfMonths).toSeq
    val currencies = Seq("SGD", "IDR")

    val groupedWallet = wallets.groupByDate().values
    val balanceChart = currencies.map { c =>
      c -> groupedWallet.map(_.collect { case w if w.currency == c => w.amount}.sum).scan(0)(_+_)
        .takeRight(numberOfMonths).toSeq
    }.toMap

    val expenseChart = currencies.map { c =>
      c -> expenseSet.toSeq.map { cat =>
        cat -> groupedWallet.map(_.collect { case w if w.currency == c && w.category == cat => -w.amount}.sum)
            .takeRight(numberOfMonths).toSeq
      }.toMap
    }.toMap

    val investAccounts = Seq("Amartha", "iGrow", "Growpal", "RDN", "Stock")
    val investChart = investAccounts.map { acct =>
      acct -> groupedWallet.map(_.collect { case w if w.category == "Funding" && w.name == acct => -w.amount }.sum)
        .scan(0)(_+_).takeRight(numberOfMonths).toSeq
    }.toMap

    val passiveChart =
      investAccounts.map { acct =>
        acct -> groupedWallet.map(_.collect { case w if w.category == "ROI" && w.name == acct => w.amount }.sum)
          .takeRight(numberOfMonths).toSeq
      }.toMap

    DashboardView(
      savingAccount,
      Pie(pie._1, pie._2),
      Chart(monthsLabel, balanceChart, expenseChart, investChart, passiveChart)
    )
  }

  def data(secretKey: String, date: Option[Int]): DataView = {
    val wallets = authAndAwait(secretKey, walletRepo.getAll)

    val requestDate = date.getOrElse(todayDate)
    val nextDate = adjustDate(requestDate+1).toString
    val prevDate = adjustDate(requestDate-1).toString

    val requestedMonth = requestDate % 100
    val monthString = getMonth(requestedMonth)
    val yearString = (requestDate / 100).toString
    val cmsData = CMSData(requestDate, monthString, yearString, nextDate, prevDate)

    val walletResult = wallets.filter(_.date == requestDate)
    val SGD = calculateBalance(wallets, requestDate, "DBS")
    val IDR = calculateBalance(wallets, requestDate, "BCA")

    DataView(cmsData, walletResult, SGD, IDR)
  }

  def login(secretKey: String): Boolean = secretKey == SECRET_KEY

  def create(secretKey: String, date: Int, fields: Map[String, String]): Int = {
    val name = fields.getOrElse("name", throw new Exception("name not found"))
    val category = fields.getOrElse("category", throw new Exception("category not found"))
    val currency = fields.getOrElse("currency", throw new Exception("currency not found"))
    val amount = fields.get("amount").map(_.toInt).getOrElse(throw new Exception("amount not found"))
    val done = fields.get("done") match {
      case Some(s) if s == "on" => true
      case _ => false
    }
    val account = fields.getOrElse("account", throw new Exception("account not found"))

    val input = Wallet(0, date, name, category, currency, amount, done, account)
    authAndAwait(secretKey, walletRepo.insert(input))
  }

  def delete(secretKey: String, id: Int): Int = authAndAwait(secretKey, walletRepo.delete(id))

  private def authAndAwait[T](secretKey: String, f: Future[T]): T = {
    secretKey match {
      case SECRET_KEY => Await.result(f, Duration.Inf)
      case _ => throw new Exception("Wrong secret key") // TODO: need better handle
    }
  }

  implicit class GroupDate(wallets: Seq[Wallet]) {
    def groupByDate(): SortedMap[Int, Seq[Wallet]] = wallets.filter(_.date <= todayDate.toInt).groupBy(_.date).to(SortedMap)
  }

  implicit class DoubleHelper(d: Double) {
    def round2Digits(): Double = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private def calculateBalance(wallets: Seq[Wallet], requestDate: Int, account: String): Balance = {
    val beginning = wallets.filter(w => w.date < requestDate && w.account == account).map(_.amount).sum
    val summary = wallets.filter(w => w.date == requestDate && w.account == account)
    val plannedEnding = beginning + summary.map(_.amount).sum
    val realEnding = beginning + summary.collect { case w if w.done => w.amount }.sum
    Balance(beginning.formatNumber, plannedEnding.formatNumber, realEnding.formatNumber)
  }

  private def adjustDate(date: Int): Int = {
    date % 100 match {
      case 13 =>(date / 100 + 1) * 100 + 1
      case 0 => (date / 100 - 1) * 100 + 12
      case _ => date
    }
  }

  private def adjustWallet(wallets: Seq[Wallet]): Seq[Wallet] = {
    /**
      * this const to be divided to SGD
      * 1 MYR = 7.6011 THB
      * 1 SGD = 23.5 THB
      * 1 SGD = 3.0916 MYR
      * 1 SGD = 10,290 IDR
      */
    val MYR = 3.0916
    val THB	= 23.5
    val IDR =	10290

    wallets.map(w => w.copy(amount = w.currency match {
      case "MYR" => (w.amount / MYR).toInt
      case "THB" => (w.amount / THB).toInt
      case "IDR" => w.amount / IDR
      case _ => w.amount
    }))
  }

  protected lazy val todayDate: Int = {
    val now = Calendar.getInstance()
    val thisMonth = now.get(Calendar.MONTH) + 1
    val thisMonthStringNumber = thisMonth match {
      case x if x < 10 => "0" + x.toString
      case x => x.toString
    }
    val thisYear = now.get(Calendar.YEAR)
    (thisYear.toString + thisMonthStringNumber).toInt
  }

  implicit class Formatter(in: Int) {
    def formatNumber: String = {
      val formatter = NumberFormat.getIntegerInstance
      formatter.format(in)
    }
  }

  private[service] def getMonth(i: Int): String = {
    i match {
      case 1 => "January"
      case 2 => "February"
      case 3 => "March"
      case 4 => "April"
      case 5 => "May"
      case 6 => "June"
      case 7 => "July"
      case 8 => "August"
      case 9 => "September"
      case 10 => "October"
      case 11 => "November"
      case 12 => "December"
    }
  }

}
