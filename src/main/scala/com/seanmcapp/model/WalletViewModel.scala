package com.seanmcapp.model

case class DashboardBalance(date: Int, sum: Int)
case class DashboardChart(balance: List[DashboardBalance], lastYearExpenses: Map[String, Int], ytdExpenses: Map[String, Int])
case class DashboardSavings(dbs: Int, bca: Int)
case class DashboardPlanned(sgd: Int, idr: Int)
case class DashboardWallet(id: Option[Int] = None, date: Int, name: String, category: String, currency: String, amount: Int, done: Boolean, account: String)
case class DashboardView(chart: DashboardChart, savings: DashboardSavings, planned: DashboardPlanned, detail: List[DashboardWallet])
