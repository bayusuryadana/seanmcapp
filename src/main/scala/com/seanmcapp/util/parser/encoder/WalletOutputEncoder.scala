package com.seanmcapp.util.parser.encoder

import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.WalletCommon

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Option[WalletResponse])

case class WalletResponse(rawData: Seq[Wallet], investAccount: InvestsAccount, savingsAccount: SavingsAccount,
                          label: Seq[Int], chartData: ChartData, pie: Map[String, Int])

case class InvestsAccount(amartha: Int, igrow: Int, growpal: Int, plastik: Int)

case class SavingsAccount(myr: Int, thb: Int, sgd: Int, idr: Int)

case class ChartData(cashFlow: Seq[Int], balance: Seq[Int], expense: Expense, activeInvest: ActiveInvest, investIncome: Seq[Int])

case class Expense(daily: Seq[Int], rent: Seq[Int], zakat: Seq[Int], travel: Seq[Int], fashion: Seq[Int], itStuff: Seq[Int], misc: Seq[Int], games: Seq[Int])

case class ActiveInvest(amartha: Seq[Int], igrow: Seq[Int], growpal: Seq[Int], plastik: Seq[Int])

trait WalletOutputEncoder extends WalletCommon {

  implicit val activeInvestFormat = jsonFormat4(ActiveInvest)

  implicit val expenseFormat = jsonFormat8(Expense)

  implicit val chartDataFormat = jsonFormat5(ChartData)

  implicit val savingsAccountFormat = jsonFormat4(SavingsAccount)

  implicit val investsAccountFormat = jsonFormat4(InvestsAccount)

  implicit val walletResponseFormat = jsonFormat6(WalletResponse)

  implicit val walletOutputFormat = jsonFormat4(WalletOutput)

}
