package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class TelegramConf(endpoint: String, botname: String)

object TelegramConf extends Configuration[TelegramConf] {

  override val prefix = "telegram"

  def apply(): TelegramConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): TelegramConf = {
    TelegramConf(
      Try(c.getString("bot-endpoint")).getOrElse(""),
      Try(c.getString("bot-name")).getOrElse("")
    )
  }
}
