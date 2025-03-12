package com.seanmcapp.service

import com.seanmcapp.repository.Wallet

case class DashboardView(savingAccount: Map[String, String], // [currency, amount]
                         pie: Pie,
                         chart: Chart)

case class Pie(label: Seq[String], data: Seq[Double])

case class Chart(label: Seq[Int],
                 balance: Map[String, Seq[Int]],
                 lastYearExpenses: Map[String, Int],
                 ytdExpenses: Map[String, Int]
                )

case class DataView(cmsData: CMSData, wallet: Seq[Wallet], sgdBalance: Balance, idrBalance: Balance, savingAccount: Map[String, String])

case class CMSData(currentDate: Int, thisMonth: String, thisYear: String, nextDate: String, prevDate: String)

case class Balance(beginning: String, plannedEnding: String, realEnding: String)
case class StockView(quarter: Int)
