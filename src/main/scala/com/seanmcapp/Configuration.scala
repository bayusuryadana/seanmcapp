package com.seanmcapp

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

// $COVERAGE-OFF$
abstract class Configuration[T](prefix: String) {

  def apply(): T = {
    val config = Try(ConfigFactory.load.getConfig(prefix)).getOrElse(ConfigFactory.empty())
    buildConfig(config)
  }

  def buildConfig(c: Config): T

}

case class DatabaseConf(name: String, host: String, user: String, pass: String)
object DatabaseConf extends Configuration[DatabaseConf]("database") {
  override def buildConfig(c: Config): DatabaseConf = {
    // if there is no variable env created, will fallback to local setup
    DatabaseConf(
      Try(c.getString("name")).getOrElse("postgres"),
      Try(c.getString("host")).getOrElse("localhost"),
      Try(c.getString("user")).getOrElse("postgres"),
      Try(c.getString("pass")).getOrElse("password")
    )
  }
}

case class HttpConf(connTimeout: Int, readTimeout: Int, followRedirects: Boolean)
object HttpConf extends Configuration[HttpConf]("http") {
  override def buildConfig(c: Config): HttpConf = {
    HttpConf(
      Try(c.getInt("conn-timeout")).getOrElse(1000),
      Try(c.getInt("read-timeout")).getOrElse(5000),
      Try(c.getBoolean("follow-redirects")).getOrElse(false)
    )
  }
}

case class TelegramConf(endpoint: String, botname: String)
object TelegramConf extends Configuration[TelegramConf]("telegram") {
  override def buildConfig(c: Config): TelegramConf = {
    TelegramConf(
      Try(c.getString("bot-endpoint")).getOrElse(""),
      Try(c.getString("bot-name")).getOrElse("")
    )
  }
}

case class WalletConf(secretKey: String)
object WalletConf extends Configuration[WalletConf]("wallet") {
  override def buildConfig(c: Config): WalletConf = {
    WalletConf(Try(c.getString("secret-key")).getOrElse(""))
  }
}
