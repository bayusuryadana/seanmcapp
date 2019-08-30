package com.seanmcapp.util.parser

import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.decoder.Decoder
import com.seanmcapp.util.parser.encoder.Encoder

trait WalletCommon extends Encoder with Decoder {
  implicit val walletFormat = jsonFormat5(Wallet)
}
