package com.seanmcapp.service

import com.seanmcapp.model.{DashboardBalance, DashboardChart, DashboardPlanned, DashboardSavings, DashboardView, DashboardWallet}

import com.seanmcapp.util.AppsConf
import com.seanmcapp.repository.{Balance, Expenses, Wallet, WalletRepo}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WalletService(walletRepo: WalletRepo) {

//  private val activeIncomeSet = Set("Salary", "Bonus")
//  private val expenseSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Wellness", "Funding")

  private[service] val SECRET_KEY = AppsConf().secretKey

  def dashboard(date: Int): Future[DashboardView] = {
    for {
      dbsBalances <- walletRepo.getChartBalance("DBS", date)
      lastYearExpenses <- walletRepo.getExpenses((date / 100) - 1).map(simplifiedExpenses)
      ytdExpenses <- walletRepo.getExpenses(date / 100).map(simplifiedExpenses)
      currentDBS <- walletRepo.getMonthlyBalance("DBS")
      currentBCA <- walletRepo.getMonthlyBalance("BCA")
      plannedDBS <- walletRepo.getMonthlyBalance("DBS", Some(date))
      plannedBCA <- walletRepo.getMonthlyBalance("BCA", Some(date))
      thisMonthData <- walletRepo.getWalletByDate(date)
    } yield {
      val dashboardBalance = dbsBalances.map { balance =>
        (DashboardBalance.apply _).tupled(Balance.unapply(balance).get)
      }
      val dashboardWallet = thisMonthData.map { wallet =>
        (DashboardWallet.apply _).tupled(Wallet.unapply(wallet).get)
      }
      DashboardView(
        DashboardChart(dashboardBalance, lastYearExpenses, ytdExpenses),
        DashboardSavings(currentDBS, currentBCA),
        DashboardPlanned(plannedDBS, plannedBCA),
        dashboardWallet
      )
    }
  }

  // TODO: do category from SQL itself
  private def simplifiedExpenses(expenses: List[Expenses]): Map[String, Int] = {
    expenses.groupBy(_.category).map { case (key, value) =>
      key -> value.map(_.amount).sum
    }
  }

  def create(wallet: DashboardWallet): Future[Int] = {
    val walletRecord = (Wallet.apply _).tupled(DashboardWallet.unapply(wallet).get)
    walletRepo.insert(walletRecord)
  }

  def update(wallet: DashboardWallet): Future[Int] = {
    val walletRecord = (Wallet.apply _).tupled(DashboardWallet.unapply(wallet).get)
    walletRepo.update(walletRecord)
  }

  def delete(id: Int): Future[Int] = walletRepo.delete(id)

}
