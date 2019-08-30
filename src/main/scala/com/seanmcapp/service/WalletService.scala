package com.seanmcapp.service

import com.seanmcapp.config.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}
import com.seanmcapp.util.parser.WalletCommon
import spray.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WalletService(walletRepo: WalletRepo) extends WalletCommon {

  private val SECRET_KEY = WalletConf().secretKey

  def getAll(implicit secretKey: String): Future[Seq[Wallet]] = {
    if (secretKey == SECRET_KEY) {
      for {
        wallet <- walletRepo.getAll
      } yield {
        wallet
      }
    } else {
      Future.failed(new Exception("Wrong secret key :("))
    }
  }

  def insert(payload: JsValue)(implicit secretKey: String): Future[Int] = {
    if (secretKey == SECRET_KEY) {
      val wallet = decode[Wallet](payload)
      for {
        wallet <- walletRepo.insert(wallet)
      } yield {
        wallet
      }
    } else {
      Future.failed(new Exception("Wrong secret key :("))
    }
  }

}
