package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class WalletConf(secretKey: String)

object WalletConf extends Configuration[WalletConf] {
  override val prefix: String = "wallet"

  def apply(): WalletConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): WalletConf = {
    WalletConf(Try(c.getString("secret-key")).getOrElse(""))
  }
}
