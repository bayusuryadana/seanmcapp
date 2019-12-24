package com.seanmcapp.mock.repository

import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}

import scala.concurrent.Future

object WalletRepoMock extends WalletRepo {

  private val walletList: Seq[Wallet] = Seq(
    Wallet(1, 201909, "Japan 2020", "Travel", "SGD", 850, false, "DBS"),
    Wallet(2, 201909, "Condo Rent", "Rent", "SGD", 700, true, "DBS"),
    Wallet(3, 201909, "Daily", "Daily", "SGD", 745, false, "DBS")
  )

  override def getAll: Future[Seq[Wallet]] = Future.successful(walletList)

  override def insert(wallet: Wallet): Future[Wallet] = Future.successful(wallet)

  override def update(wallet: Wallet): Future[Int] = Future.successful(1)

  override def delete(id: Int): Future[Int] = Future.successful(1)

}
