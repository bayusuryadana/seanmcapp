package com.seanmcapp.service

import com.seanmcapp.repository.seanmcwallet.Wallet

case class DashboardView(savingAccount: Map[String, String], // [currency, amount]
                         pie: Pie,
                         chart: Chart)

case class Pie(label: Seq[String], data: Seq[Double])

case class Chart(label: Seq[Int],
                 balance: Map[String, Seq[Int]],
                 expense: Expense
                )

case class Expense(allTime: Map[String, Map[String, Seq[Int]]], lastYear: Map[String, Map[String, Seq[Int]]], ytd: Map[String, Map[String, Seq[Int]]])

case class DataView(cmsData: CMSData, wallet: Seq[Wallet], sgdBalance: Balance, idrBalance: Balance, savingAccount: Map[String, String])

case class CMSData(currentDate: Int, thisMonth: String, thisYear: String, nextDate: String, prevDate: String)

case class Balance(beginning: String, plannedEnding: String, realEnding: String)
