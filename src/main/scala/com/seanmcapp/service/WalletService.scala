package com.seanmcapp.service

import java.util.Calendar

import com.seanmcapp.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}
import com.seanmcapp.service.WalletUtils._

import scala.collection.SortedMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Seq[Wallet])

class WalletService(walletRepo: WalletRepo, walletRepoDemo: WalletRepo) {

  private val activeIncomeSet = Set("Salary", "Bonus")
  private val expenseSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Wellness", "Funding")

  private[service] val SECRET_KEY = WalletConf().secretKey
  private val TEST_KEY = "test"

  def dashboard(implicit secretKey: String): DashboardView = {
    val wallets = authAndAwait(secretKey, walletRepo.getAll, walletRepoDemo.getAll)
    
    val savingAccount = getSavingAccount(wallets)

    // pie data is based on SGD
    val adjWallet = adjustWallet(wallets)
    val pieMap = adjWallet.filter(w => w.done && expenseSet.contains(w.category)).groupBy(_.category).toSeq
      .map(cat => (cat._1, cat._2.map(-_.amount).sum))
    val totalIncome = adjWallet.filter(w => w.done && activeIncomeSet.contains(w.category)).map(_.amount).sum.toDouble
    val pie = pieMap.map(i => (i._1, (i._2 / totalIncome * 100).round2Digits())).unzip

    val numberOfMonths = 12
    val monthsLabel = wallets.groupByDate().keys.takeRight(numberOfMonths).toSeq
    val currencies = Seq("SGD", "IDR")
    val groupedWallet = wallets.groupByDate()
    val balanceChart = currencies.map { c =>
      c -> groupedWallet.values.map(_.collect { case w if w.currency == c => w.amount}.sum).scan(0)(_+_)
        .takeRight(numberOfMonths).toSeq
    }.toMap

    // expenses chart based in SGD
    val lastYearExpenses =
      expenseSet.toSeq.map { cat =>
        cat -> groupedWallet.collect { case (k, v) if k / 100 == (todayDate / 100) - 1 =>
          v.collect { case d if d.category == cat => d.amount }
        }.flatten.sum
      }.toMap
    
    val ytdExpenses = 
      expenseSet.toSeq.map { cat =>
        cat -> groupedWallet.collect { case (k, v) if k / 100 == todayDate / 100 =>
          v.collect { case d if d.category == cat => d.amount }
        }.flatten.sum
      }.toMap

    DashboardView(
      savingAccount,
      Pie(pie._1, pie._2),
      Chart(monthsLabel, balanceChart, lastYearExpenses, ytdExpenses)
    )
  }

  def data(secretKey: String, date: Option[Int]): DataView = {
    val wallets = authAndAwait(secretKey, walletRepo.getAll, walletRepoDemo.getAll)

    val requestDate = date.getOrElse(todayDate)
    val nextDate = (requestDate+1).adjustDate.toString
    val prevDate = (requestDate-1).adjustDate.toString

    val requestedMonth = requestDate % 100
    val monthString = getMonth(requestedMonth)
    val yearString = (requestDate / 100).toString
    val cmsData = CMSData(requestDate, monthString, yearString, nextDate, prevDate)

    val walletResult = wallets.filter(_.date == requestDate)
    val SGD = calculateBalance(wallets, requestDate, "DBS")
    val IDR = calculateBalance(wallets, requestDate, "BCA")

    val savingAccount = getSavingAccount(wallets)

    DataView(cmsData, walletResult, SGD, IDR, savingAccount)
  }

  // $COVERAGE-OFF$
  def login(secretKey: String): Boolean = secretKey == SECRET_KEY || secretKey == TEST_KEY

  def create(secretKey: String, date: Int, fields: Map[String, String]): Int = {
    val wallet = parseInput(date, fields)
    println(s"[WALLET][CREATE] ${wallet.toJsonString()}")
    authAndAwait(secretKey, walletRepo.insert(wallet), walletRepoDemo.insert(wallet))
  }

  def update(secretKey: String, date: Int, fields: Map[String, String]): Int = {
    val wallet = parseInput(date, fields)
    if (wallet.id == 0) throw new Exception("id not found")
    println(s"[WALLET][UPDATE] ${wallet.toJsonString()}")
    authAndAwait(secretKey, walletRepo.update(wallet), walletRepoDemo.update(wallet))
  }

  def delete(secretKey: String, id: Int): Int = {
    println(s"[WALLET][DELETE] $id")
    authAndAwait(secretKey, walletRepo.delete(id), walletRepoDemo.delete(id))
  }
  // $COVERAGE-ON$

  private def authAndAwait[T](secretKey: String, f: Future[T], tf: Future[T]): T = {
    secretKey match {
      case SECRET_KEY => Await.result(f, Duration.Inf)
      case TEST_KEY => Await.result(tf, Duration.Inf)
      case _ => throw new Exception("Wrong secret key") // TODO: need better handle
    }
  }
  
  def getSavingAccount(wallets: Seq[Wallet]): Map[String, String] = {
    def sumAccount(account: String): Int = wallets.collect { case w if w.done && w.account == account => w.amount }.sum
    val sgd = sumAccount("DBS").formatNumber
    val idr = sumAccount("BCA").formatNumber
    Map(
      "SGD" -> sgd,
      "IDR" -> idr
    )
  }

  implicit class GroupDate(wallets: Seq[Wallet]) {
    def groupByDate(): SortedMap[Int, Seq[Wallet]] = wallets.filter(_.date <= todayDate).groupBy(_.date).to(SortedMap)
  }

  implicit class DoubleHelper(d: Double) {
    def round2Digits(): Double = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  private[service] def parseInput(date: Int, fields: Map[String, String]): Wallet = {
    val id = fields.get("id").map(_.toInt).getOrElse(0)
    val name = fields.getOrElse("name", throw new Exception("name not found"))
    val category = fields.getOrElse("category", throw new Exception("category not found"))
    val currency = fields.getOrElse("currency", throw new Exception("currency not found"))
    val amount = fields.get("amount").map(_.toInt).getOrElse(throw new Exception("amount not found"))
    val done = fields.get("done") match {
      case Some(s) if s == "on" => true
      case _ => false
    }
    val account = fields.getOrElse("account", throw new Exception("account not found"))

    Wallet(id, date, name, category, currency, amount, done, account)
  }

  private def calculateBalance(wallets: Seq[Wallet], requestDate: Int, account: String): Balance = {
    val beginning = wallets.filter(w => w.date < requestDate && w.account == account).map(_.amount).sum
    val summary = wallets.filter(w => w.date == requestDate && w.account == account)
    val plannedEnding = beginning + summary.map(_.amount).sum
    val realEnding = beginning + summary.collect { case w if w.done => w.amount }.sum
    Balance(beginning.formatNumber, plannedEnding.formatNumber, realEnding.formatNumber)
  }

  private def adjustWallet(wallets: Seq[Wallet]): Seq[Wallet] = {
    wallets.map(w => w.copy(amount = w.currency match {
      case "MYR" => (w.amount / ConversionConstants.MYR).toInt
      case "THB" => (w.amount / ConversionConstants.THB).toInt
      case "IDR" => w.amount / ConversionConstants.IDR
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

object ConversionConstants {
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
}
