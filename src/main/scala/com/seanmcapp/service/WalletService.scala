package com.seanmcapp.service

import com.seanmcapp.config.WalletConf
import com.seanmcapp.repository.seanmcwallet.{Wallet, WalletRepo}
import com.seanmcapp.util.parser.{WalletCommon, WalletOutput}
import spray.json.JsValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WalletService(walletRepo: WalletRepo) extends WalletCommon {

  private[service] val SECRET_KEY = WalletConf().secretKey

  def getAll(implicit secretKey: String): Future[WalletOutput] = {
    auth(secretKey, walletRepo.getAll.map(wallet => WalletOutput(200, None, Some(wallet.size), wallet)))
  }

  def insert(payload: JsValue)(implicit secretKey: String): Future[WalletOutput] = {
    val walletInput = decode[Wallet](payload)
    auth(secretKey, walletRepo.insert(walletInput).map(wallet => WalletOutput(200, None, Some(1), Seq(wallet))))
  }

  def update(payload: JsValue)(implicit secretKey: String): Future[WalletOutput] = {
    val walletInput = decode[Wallet](payload)
    auth(secretKey, walletRepo.update(walletInput).map(wallet => WalletOutput(200, None, Some(wallet), Seq.empty[Wallet])))
  }

  def delete(id: Int)(implicit secretKey: String): Future[WalletOutput] = {
    auth(secretKey, walletRepo.delete(id).map(wallet => WalletOutput(200, None, Some(wallet), Seq.empty[Wallet])))
  }

  private def auth(secretKey: String, f: Future[WalletOutput]): Future[WalletOutput] = {
    secretKey match {
      case SECRET_KEY => f
      case _ => Future.successful(WalletOutput(403, Some("Wrong secret key"), None, Seq.empty[Wallet]))
    }
  }

}
