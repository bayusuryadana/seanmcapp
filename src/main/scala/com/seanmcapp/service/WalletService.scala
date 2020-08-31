package com.seanmcapp.service

import java.text.NumberFormat
import java.util.Calendar

import com.seanmcapp.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Seq[Wallet])

class WalletService(walletRepo: WalletRepo) {

  private val expensesSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Wellness")
  private val investSet = Set("Funding")
  private val activeIncomeSet = Set("Salary", "Bonus")

  private val pieSet = expensesSet ++ investSet

  private[service] val SECRET_KEY = WalletConf().secretKey

  def dashboard(implicit secretKey: String): DashboardView = {
    val wallets = authAndAwait(secretKey, walletRepo.getAll)
    val numberOfMonths = 13

    def sumAccount(account: String): Int = wallets.filter(w => w.done && w.account == account).map(_.amount).sum
    val amartha = sumAccount("Amartha")
    val igrow = sumAccount("iGrow")
    val growpal = sumAccount("Growpal")
    val investAmount = List(amartha, igrow, growpal).sum // TODO: will use it soon

    val myr = sumAccount("Maybank")
    val thb = sumAccount("SCB")
    val sgd = sumAccount("DBS")
    val idr = sumAccount("BCA")

    // all of these data is based on SGD
    // must use floating number to be acurate
    val adjWallet = adjustWallet(wallets)

    def groupingData(wallets: Seq[Wallet]): Seq[(Int, Seq[Wallet])] = wallets.groupBy(_.date).toSeq.filter(_._1 <= todayDate.toInt).sortBy(_._1)
    val monthsLabel = groupingData(adjWallet).map(_._1).takeRight(numberOfMonths)
    val cashFlowData = groupingData(adjWallet).map(_._2.map(_.amount).sum).takeRight(numberOfMonths)
    val balanceData = groupingData(adjWallet).map(_._2.map(_.amount).sum).scan(0)(_+_).takeRight(numberOfMonths)
    val expenseByCatSeq = groupingData(adjWallet).map(date => date._2.filter(row => expensesSet.contains(row.category))
      .groupBy(_.category).toSeq.map(cat => (cat._1, -cat._2.map(_.amount).sum)).toMap)
    def expense(cat: String): Seq[Int] = expenseByCatSeq.map(_.getOrElse(cat, 0)).takeRight(7)
    val expenseByCatData = Expense(expense("Daily"), expense("Rent"), expense("Zakat"),
      expense("Travel"), expense("Fashion"), expense("IT Stuff"), expense("Misc"),
      expense("Wellness"))

    val activeInvestSeq = groupingData(wallets).scanLeft((0, 0, 0)) { (res, date) =>
      def sumData(platform: String) = date._2.filter(data => data.category == "Funding" && data.account == platform && data.done).map(-_.amount).sum
      (res._1 + sumData("Amartha"), res._2 + sumData("iGrow"), res._3 + sumData("Growpal"))
    }.tail
    val activeInvest = ActiveInvest(
      activeInvestSeq.map(_._1).takeRight(numberOfMonths),
      activeInvestSeq.map(_._2).takeRight(numberOfMonths),
      activeInvestSeq.map(_._3).takeRight(numberOfMonths)
    )
    val investIncome = groupingData(wallets).map(_._2.collect { case w if w.category == "ROI" => w.amount}.sum)
      .takeRight(numberOfMonths)

    val pieMap = adjWallet.filter(w => w.done && pieSet.contains(w.category)).groupBy(_.category).toSeq
      .map(cat => (cat._1, cat._2.map(-_.amount).sum))
    val totalIncome = adjWallet.filter(w => w.done && activeIncomeSet.contains(w.category)).map(_.amount).sum.toDouble
    val pie = pieMap.map(i => (i._1, (i._2 / totalIncome * 10000).toInt / 100.0 )).unzip

    DashboardView(
      InvestAccount(amartha.formatNumber, igrow.formatNumber, growpal.formatNumber),
      SavingAccount(myr.formatNumber, thb.formatNumber, sgd.formatNumber, idr.formatNumber),
      monthsLabel,
      ChartData(cashFlowData, balanceData, expenseByCatData, activeInvest, investIncome),
      Pie(pie)
    )
  }

  def data(secretKey: String, date: Option[Int]): DataView = {
    val wallets = authAndAwait(secretKey, walletRepo.getAll)

    val requestDate = date.getOrElse(todayDate)
    val nextDate = adjustDate(requestDate+1).toString
    val prevDate = adjustDate(requestDate-1).toString

    val requestedMonth = requestDate % 100
    val monthString = requestedMonth match {
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
    val yearString = (requestDate / 100).toString
    val cmsData = CMSData(monthString, yearString, nextDate, prevDate)

    val walletResult = wallets.filter(_.date == requestDate)
    val SGD = calculateBalance(wallets, requestDate, "DBS")
    val IDR = calculateBalance(wallets, requestDate, "BCA")

    DataView(cmsData, walletResult, SGD, IDR)
  }

  def insert(walletInput: Wallet)(implicit secretKey: String): Int = {
    authAndAwait(secretKey, walletRepo.insert(walletInput))
  }

  def update(walletInput: Wallet)(implicit secretKey: String): Int = {
    authAndAwait(secretKey, walletRepo.update(walletInput))
  }

  def delete(id: Int)(implicit secretKey: String): Int = {
    authAndAwait(secretKey, walletRepo.delete(id))
  }

  private def authAndAwait[T](secretKey: String, f: Future[T]): T = {
    secretKey match {
      case SECRET_KEY => Await.result(f, Duration.Inf)
      case _ => throw new Exception("Wrong secret key") // TODO: need better handle
    }
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
    // this const will exchange to SGD
    val MYR = 3.087415185
    val THB	= 23.47115051
    val IDR =	10370

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

}
