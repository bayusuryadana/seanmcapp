package com.seanmcapp.service

import java.util.Calendar
import com.seanmcapp.util.AppsConf
import com.seanmcapp.repository.{Wallet, WalletRepo}
import com.seanmcapp.service.WalletUtils._

import scala.collection.SortedMap
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class WalletService(walletRepo: WalletRepo) {

  private val activeIncomeSet = Set("Salary", "Bonus")
  private val expenseSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Wellness", "Funding")

  private[service] val SECRET_KEY = AppsConf().secretKey

  def dashboard(implicit secretKey: String): DashboardView = {
    val wallets = authAndAwait(secretKey, (r: WalletRepo) => { r.getAll })
    
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

    def filterAndConvertWallet(v: Seq[Wallet], cat: String): Seq[Int] = {
      v.collect { case d if d.category == cat => -convertCurrencyToSGD(d.amount, d.currency) }
    }
    
    // expenses chart based in SGD
    val lastYearExpenses =
      expenseSet.toSeq.map { cat =>
        cat -> groupedWallet.collect { 
          case (k, v) if k / 100 == (todayDate / 100) - 1 => filterAndConvertWallet(v, cat)
        }.flatten.sum
      }.toMap
    
    val ytdExpenses = 
      expenseSet.toSeq.map { cat =>
        cat -> groupedWallet.collect { 
          case (k, v) if k / 100 == todayDate / 100 => filterAndConvertWallet(v, cat)
        }.flatten.sum
      }.toMap

    DashboardView(
      savingAccount,
      Pie(pie._1, pie._2),
      Chart(monthsLabel, balanceChart, lastYearExpenses, ytdExpenses)
    )
  }

  def data(secretKey: String, date: Option[Int]): DataView = {
    val wallets = authAndAwait(secretKey, (r: WalletRepo) => { r.getAll })

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
  def login(secretKey: String): Boolean = secretKey == SECRET_KEY

  def create(secretKey: String, fields: Map[String, String]): Int = {
    val wallet = parseInput(fields)
    println(s"[WALLET][CREATE] ${wallet}")
    authAndAwait(secretKey, (r: WalletRepo) => { r.insert(wallet).map(_ => wallet.date) })
  }

  def update(secretKey: String, fields: Map[String, String]): Int = {
    val wallet = parseInput(fields)
    println(s"[WALLET][UPDATE] ${wallet}")
    authAndAwait(secretKey, (r: WalletRepo) => { r.update(wallet).map(_ => wallet.date) })
  }

  def delete(secretKey: String, fields: Map[String, String]): Int = {
    val id = fields.getField("id").tryToInt
    val date = fields.getField("date").tryToInt
    println(s"[WALLET][DELETE] $id")
    authAndAwait(secretKey, (r: WalletRepo) => { r.delete(id).map(_ => date) })
  }
  // $COVERAGE-ON$

  private def authAndAwait[T](secretKey: String, f: WalletRepo => Future[T]): T = {
    val wr = secretKey match {
      case SECRET_KEY => walletRepo
      case _ => throw new Exception("wrong password.")
    }
    Await.result(f(wr), Duration.Inf)
  }
  
  private def getSavingAccount(wallets: Seq[Wallet]): Map[String, String] = {
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

  implicit class StringHelper(s: String) {
    def tryToInt: Int = s.toIntOption.getOrElse(throw new Exception(s"$s cannot be parsed to Int"))
  }
  
  implicit class PostFormFieldsHelper(fields: Map[String, String]) {
    def getField(key: String): String = fields.getOrElse(key, throw new Exception(s"$key not found"))
  }

  private[service] def parseInput(fields: Map[String, String]): Wallet = {
    
    val date = fields.getField("date").tryToInt

    val id = fields.getField("id").tryToInt
    val name = fields.getField("name")
    val category = fields.getField("category")
    val currency = fields.getField("currency")
    val amount = fields.getField("amount").tryToInt
    val done = fields.get("done").contains("on")
    val account = fields.getField("account")

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
    wallets.map(w => w.copy(amount = convertCurrencyToSGD(w.amount, w.currency)))
  }
  
  private def convertCurrencyToSGD(amount: Int, currency: String): Int = {
    currency match {
      case "MYR" => (amount / ConversionConstants.MYR).toInt
      case "THB" => (amount / ConversionConstants.THB).toInt
      case "IDR" => amount / ConversionConstants.IDR
      case _ => amount
    }
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
