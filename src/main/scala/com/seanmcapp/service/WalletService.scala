package com.seanmcapp.service

import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WalletService(walletRepo: WalletRepo) {

  def getAll(secretKey: String): Future[Seq[Wallet]] = {
    for {
      wallet <- walletRepo.getAll
    } yield {
      wallet
    }
  }

}
