package com.seanmcapp.service

import com.seanmcapp.config.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}
import com.seanmcapp.util.parser.WalletCommon
import com.seanmcapp.util.parser.encoder.WalletOutput
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
    auth(secretKey, walletRepo.insert(walletInput).map(wallet => WalletOutput(200, None, Some(1), Some(Seq(wallet)))))
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

    val investCategorySet
    val nonExpensesSet = Set("Salary", "Bonus", "Temp", "Invest")
    // this will exchange to SGD
    val MYR = 3.087415185
    val THB	= 23.47115051
    val IDR =	10370

    // all of these data is based on SGD
    // must use floating number to be acurate
    val adjWallet = wallets.map(w => w.copy(amount = w.currency match {
      case "MYR" => (w.amount / MYR).toInt
      case "THB" => (w.amount / THB).toInt
      case "IDR" => w.amount / IDR
      case _ => w.amount
    }))
    val groupedData = adjWallet.groupBy(_.date).toSeq.sortBy(_._1)
    val monthLabel = groupedData.map(_._1)
    val cashFlowData = groupedData.map(_._2).map(_.foldLeft(0)((res, row) => res + row.amount))
    val balanceData = cashFlowData.scan(0)(_+_).tail
    val expenseByCatData = groupedData.map(date => date._2.filterNot(row => nonExpensesSet.contains(row.category))
      .groupBy(_.category).map(cat => (cat._1, cat._2.foldLeft(0)((res, row) => res + row.amount))))
    val investByCatData = groupedData.map(date => date._2.filter(row => row.category == "Invest" && row.amount > 0)
      .groupBy(_.name).map(cat => (cat._1, cat._2.foldLeft(0)((res, row) => res + row.amount))))

    def currentWallet(curr: String) = wallets.filter(_.done).filter(_.currency == curr).map(_.amount).sum
    val myr = currentWallet("MYR")
    val thb = currentWallet("THB")
    val sgd = currentWallet("SGD")
    val idr = currentWallet("IDR")

    //println(s"${monthLabel.size}\n${cashFlowData.size}\n${balanceData.size}\n${expenseByCatData.size}")
    //println(s"$myr\n$thb\n$sgd\n$idr")
    //raw_data

    WalletOutput(200, None, Some(wallets.size), Some(wallets))
  }

}
