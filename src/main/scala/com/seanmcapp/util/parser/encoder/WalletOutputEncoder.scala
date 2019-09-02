package com.seanmcapp.util.parser.encoder

import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.WalletCommon

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Option[Seq[Wallet]])

trait WalletOutputEncoder extends WalletCommon {
  implicit val walletOutputFormat = jsonFormat4(WalletOutput)
}
