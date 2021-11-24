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

case class AirvisualConf(key: String)
object AirvisualConf extends Configuration[AirvisualConf]("airvisual") {
  override def buildConfig(c: Config): AirvisualConf = {
    AirvisualConf(Try(c.getString("api-key")).getOrElse(""))
  }
}

case class AmarthaConf(username: String, password: String)
object AmarthaConf extends Configuration[AmarthaConf]("amartha") {
  override def buildConfig(c: Config): AmarthaConf = {
    AmarthaConf(
      Try(c.getString("username")).getOrElse(""),
      Try(c.getString("password")).getOrElse("")
    )
  }
}

case class BroadcastConf(secretKey: String)
object BroadcastConf extends Configuration[BroadcastConf]("broadcast") {
  override def buildConfig(c: Config): BroadcastConf = {
    BroadcastConf(Try(c.getString("secret-key")).getOrElse(""))
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

case class StorageConf(access: String, secret: String, host: String, bucket: String)
object StorageConf extends Configuration[StorageConf]("aws") {
  override def buildConfig(c: Config): StorageConf = {
    StorageConf(
      Try(c.getString("access")).getOrElse(""),
      Try(c.getString("secret")).getOrElse(""),
      Try(c.getString("host")).getOrElse("http://localhost"),
      Try(c.getString("bucket")).getOrElse("")
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

case class InstagramConf(username: String, password: String)
object InstagramConf extends Configuration[InstagramConf]("instagram") {
  override def buildConfig(c: Config): InstagramConf = {
    InstagramConf(
      Try(c.getString("username")).getOrElse(""),
      Try(c.getString("password")).getOrElse("")
    )
  }
}

case class DiscordConf(token: String)
object DiscordConf extends Configuration[DiscordConf]("discord") {
  override def buildConfig(c: Config): DiscordConf = {
    DiscordConf(Try(c.getString("token")).getOrElse(""))
  }
}

case class HadithConf(key: String)
object HadithConf extends Configuration[HadithConf]("hadith") {
  override def buildConfig(c: Config): HadithConf = {
    HadithConf(Try(c.getString("api-key")).getOrElse(""))
  }
}

case class TwitterConf(consumerKey: String, consumerSecret: String, accessToken: String, accessSecret: String)
object TwitterConf extends Configuration[TwitterConf]("twitter") {
  override def buildConfig(c: Config): TwitterConf = {
    TwitterConf(
      Try(c.getString("consumer.key")).getOrElse(""),
      Try(c.getString("consumer.secret")).getOrElse(""),
      Try(c.getString("access.token")).getOrElse(""),
      Try(c.getString("access.secret")).getOrElse(""),
    )
  }
}
