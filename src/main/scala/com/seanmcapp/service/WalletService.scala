package com.seanmcapp.service

import com.seanmcapp.model.{DashboardBalance, DashboardChart, DashboardPlanned, DashboardSavings, DashboardView, DashboardWallet}

import com.seanmcapp.repository.{Wallet, WalletRepo}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class WalletService(walletRepo: WalletRepo) {

  private val expenseSet = Set("Daily", "Rent", "Zakat", "Travel", "Fashion", "IT Stuff", "Misc", "Wellness", "Funding")

  def dashboard(date: Int): Future[DashboardView] = {
    walletRepo.getAll.map { wallets =>
      val dashboardBalance = wallets.filter(w => w.account == "DBS" && w.date <= date).groupBy(_.date).map { case (date, items) =>
        DashboardBalance(date, items.map(_.amount).sum)
      }.toList.sortBy(_.date).scanLeft(DashboardBalance(0,0)) { (cumulativeSum, bal) =>
        bal.copy(sum = cumulativeSum.sum + bal.sum)
      }.tail.takeRight(12)

      val year = date / 100
      val lastYearExpenses = calculateCategoryAmount(wallets, year -1)
      val ytdExpenses = calculateCategoryAmount(wallets, year)

      val currentDBS = calculateTotalAmount(wallets, "DBS")
      val currentBCA = calculateTotalAmount(wallets, "BCA")

      val plannedDBS = calculateTotalAmount(wallets, "DBS", Some(date))
      val plannedBCA = calculateTotalAmount(wallets, "BCA", Some(date))

      val dashboardWalletDetail = wallets.filter(_.date == date)
        .map(wallet => (DashboardWallet.apply _).tupled(Wallet.unapply(wallet).get))

      DashboardView(
        DashboardChart(dashboardBalance, lastYearExpenses, ytdExpenses),
        DashboardSavings(currentDBS, currentBCA),
        DashboardPlanned(plannedDBS, plannedBCA),
        dashboardWalletDetail
      )
    }
  }

  private def calculateCategoryAmount(wallets: List[Wallet], year: Int): Map[String, Int] = {
    wallets.filter { w =>
      w.account == "DBS" && w.done && (w.date / 100) == year && expenseSet.contains(w.category)
    }.groupBy(_.category).map { case (category, transactions) =>
      category -> transactions.map(-_.amount).sum
    }
  }

  private def calculateTotalAmount(wallets: List[Wallet], account: String, date: Option[Int] = None): Int = {
    wallets.filter { w =>
      val filterCondition = if (date.isDefined) date.get >= w.date else w.done
      w.account == account && filterCondition
    }.map(_.amount).sum
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
