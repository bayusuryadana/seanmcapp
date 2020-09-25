package com.seanmcapp.service

import com.seanmcapp.repository.seanmcwallet.Wallet

import scala.collection.SortedMap

case class DashboardView(savingAccount: Map[String, String], // [currency, amount]
                         pie: Pie,
                         chart: Chart)

case class Pie(label: Seq[String], data: Seq[Double])

case class Chart(label: Seq[Int],
                 balance: Map[String, Seq[Int]],
                 expense: Map[String, Map[String, Seq[Int]]],
                 invest: Map[String, Seq[Int]],
                 passive: Map[String, Seq[Int]]
                )

case class DataView(cmsData: CMSData, wallet: Seq[Wallet], sgdBalance: Balance, idrBalance: Balance)

case class CMSData(thisMonth: String, thisYear: String, nextDate: String, prevDate: String)

case class Balance(beginning: String, plannedEnding: String, realEnding: String)

case class AmarthaView(totalAmountLeft: String, header: Seq[String], data: SortedMap[Long, AmarthaMitraView])

case class AmarthaMitraView(id: Long, name: String, ROI: String, numberOfRemainingPayment: Int,
                            remainingPaymentAmount: String, data: List[String])

