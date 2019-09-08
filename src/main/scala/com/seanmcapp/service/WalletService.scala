package com.seanmcapp.service

import com.seanmcapp.config.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}
import com.seanmcapp.util.parser.WalletCommon
import com.seanmcapp.util.parser.encoder._
import spray.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WalletService(walletRepo: WalletRepo) extends WalletCommon {

  private[service] val SECRET_KEY = WalletConf().secretKey

  def getAll(implicit secretKey: String): Future[WalletOutput] = {
    auth(secretKey, walletRepo.getAll.map(processWallet))
  }

  def insert(payload: JsValue)(implicit secretKey: String): Future[WalletOutput] = {
    val walletInput = decode[Wallet](payload)
    auth(secretKey, walletRepo.insert(walletInput).map(wallet => WalletOutput(200, None, Some(1), None)))
  }

  def update(payload: JsValue)(implicit secretKey: String): Future[WalletOutput] = {
    val walletInput = decode[Wallet](payload)
    auth(secretKey, walletRepo.update(walletInput).map(wallet => WalletOutput(200, None, Some(wallet), None)))
  }

  def delete(id: Int)(implicit secretKey: String): Future[WalletOutput] = {
    auth(secretKey, walletRepo.delete(id).map(wallet => WalletOutput(200, None, Some(wallet), None)))
  }

  private def auth(secretKey: String, f: Future[WalletOutput]): Future[WalletOutput] = {
    secretKey match {
      case SECRET_KEY => f
      case _ => Future.successful(WalletOutput(403, Some("Wrong secret key"), None, None))
    }
  }

  private def processWallet(wallets: Seq[Wallet]): WalletOutput = {

    val expensesSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Games")
    val investNonROISet = Set("Funding", "Turnover", "Refunding")
    // this const will exchange to SGD
    val MYR = 3.087415185
    val THB	= 23.47115051
    val IDR =	10370

    def investAccount(account: String): Int = wallets.collect { case w if w.done && w.name == account && w.category != "Funding" => w.amount}.sum
    val amartha = investAccount("Amartha")
    val igrow = investAccount("iGrow")
    val growpal = investAccount("Growpal")
    val plastik = investAccount("Plastik")
    val investAmount = Seq(amartha, igrow, growpal, plastik).sum

    def savingsAccount(curr: String) = wallets.filter(_.done).filter(_.currency == curr).map(_.amount).sum
    val myr = savingsAccount("MYR")
    val thb = savingsAccount("THB")
    val sgd = savingsAccount("SGD")
    val idr = savingsAccount("IDR") - investAmount

    // all of these data is based on SGD
    // must use floating number to be acurate
    val adjWallet = wallets.map(w => w.copy(amount = w.currency match {
      case "MYR" => (w.amount / MYR).toInt
      case "THB" => (w.amount / THB).toInt
      case "IDR" => w.amount / IDR
      case _ => w.amount
    }))
    def groupingData(wallets: Seq[Wallet]): Seq[(Int, Seq[Wallet])] = wallets.groupBy(_.date).toSeq.sortBy(_._1)
    val monthsLabel = groupingData(adjWallet).map(_._1)
    val cashFlowData = groupingData(adjWallet).map(_._2.collect { case w if !investNonROISet.contains(w.category) => w.amount}.sum)
    val balanceData = groupingData(adjWallet).map(_._2.map(_.amount).sum).scan(0)(_+_).tail
    val expenseByCatSeq = groupingData(adjWallet).map(date => date._2.filter(row => expensesSet.contains(row.category))
      .groupBy(_.category).map(cat => (cat._1, -cat._2.map(_.amount).sum)))
    def expense(cat: String): Seq[Int] = expenseByCatSeq.map(_.getOrElse(cat, 0))
    val expenseByCatData = Expense(expense("Daily"), expense("Rent"), expense("Zakat"), expense("Travel"),
      expense("Fashion"), expense("IT Stuff"), expense("Misc"), expense("Games"))

    val activeInvestSeq = groupingData(wallets).scanLeft((0, 0, 0, 0)) { (res, date) =>
      def sumData(platform: String) = date._2.collect { case w if investNonROISet.contains(w.category) && w.name == platform => -w.amount }.sum
      (res._1 + sumData("Amartha"), res._2 + sumData("iGrow"), res._3 + sumData("Growpal"), res._4 + sumData("Plastik"))
    }.tail
    val activeInvest = ActiveInvest(activeInvestSeq.map(_._1), activeInvestSeq.map(_._2), activeInvestSeq.map(_._3), activeInvestSeq.map(_._4))
    val investIncome = groupingData(wallets).map(_._2.collect { case w if w.category == "ROI" => w.amount}.sum)

    val walletResponse = WalletResponse(wallets, InvestsAccount(amartha, igrow, growpal, plastik),
      SavingsAccount(myr, thb, sgd, idr), monthsLabel, ChartData(cashFlowData, balanceData, expenseByCatData, activeInvest, investIncome))

    WalletOutput(200, None, Some(wallets.size), Some(walletResponse))
  }

}
