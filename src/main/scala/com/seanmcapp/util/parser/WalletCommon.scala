package com.seanmcapp.util.parser

import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.decoder.Decoder
import com.seanmcapp.util.parser.encoder.Encoder

case class WalletOutput(code: Int, message: Option[String], row: Option[Int], response: Seq[Wallet])

trait WalletCommon extends Encoder with Decoder {

  implicit val walletFormat = jsonFormat7(Wallet)

  implicit val walletOutputFormat = jsonFormat4(WalletOutput)

}
