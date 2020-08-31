package com.seanmcapp.service

import com.seanmcapp.repository.seanmcwallet.Wallet

case class InvestAccount(amartha: String, igrow: String, growpal: String)

case class SavingAccount(myr: String, thb: String, sgd: String, idr: String)

case class ChartData(cashFlow: Seq[Int], balance: Seq[Int], expense: Expense, activeInvest: ActiveInvest,
                     investIncome: Seq[Int])

case class Expense(daily: Seq[Int], rent: Seq[Int], zakat: Seq[Int], travel: Seq[Int], fashion: Seq[Int],
                   itStuff: Seq[Int], misc: Seq[Int], wellness: Seq[Int])

case class ActiveInvest(amartha: Seq[Int], igrow: Seq[Int], growpal: Seq[Int])

case class Pie(label: Seq[String], data: Seq[Double])
object Pie { def apply(tuple: (Seq[String], Seq[Double])): Pie = Pie(tuple._1, tuple._2) }

case class CMSData(thisMonth: String, thisYear: String, nextDate: String, prevDate: String)

case class Summary(name: String, amount: Int)

case class Balance(beginning: String, plannedEnding: String, realEnding: String)

case class DashboardView(investAccount: InvestAccount, savingAccount: SavingAccount,
                         label: Seq[Int], chartData: ChartData, pie: Pie)

case class DataView(cmsData: CMSData, wallet: Seq[Wallet], sgdBalance: Balance, idrBalance: Balance)
