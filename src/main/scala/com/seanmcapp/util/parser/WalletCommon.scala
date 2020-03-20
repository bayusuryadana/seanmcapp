package com.seanmcapp.util.parser

import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.decoder.JsonDecoder
import com.seanmcapp.util.parser.encoder.Encoder

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Seq[Wallet])

trait WalletCommon extends Encoder with JsonDecoder {

  implicit val walletFormat = jsonFormat8(Wallet)

  implicit val walletOutputFormat = jsonFormat4(WalletOutput)

}
