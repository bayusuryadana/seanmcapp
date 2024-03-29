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
object TelegramConf extends Configuration[TelegramConf]("telegram.bot-1") {
  override def buildConfig(c: Config): TelegramConf = {
    TelegramConf(
      Try(c.getString("bot-endpoint")).getOrElse(""),
      Try(c.getString("bot-name")).getOrElse("")
    )
  }
}
object TelegramConf2 extends Configuration[TelegramConf]("telegram.bot-2") {
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

case class InstagramConf(username: String, password: String, endpoint: Option[String])
object InstagramConf extends Configuration[InstagramConf]("instagram") {
  override def buildConfig(c: Config): InstagramConf = {
    InstagramConf(
      Try(c.getString("username")).getOrElse(""),
      Try(c.getString("password")).getOrElse(""),
      Try(c.getString("endpoint")).toOption
    )
  }
}

case class DiscordConf(token: String)
object DiscordConf extends Configuration[DiscordConf]("discord") {
  override def buildConfig(c: Config): DiscordConf = {
    DiscordConf(Try(c.getString("token")).getOrElse(""))
  }
}

case class HadithConf(endpoint: Option[String], key: String)
object HadithConf extends Configuration[HadithConf]("hadith") {
  override def buildConfig(c: Config): HadithConf = {
    HadithConf(
      Try(c.getString("endpoint")).toOption,
      Try(c.getString("api-key")).getOrElse("")
    )
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

case class GoogleConf(key: String)
object GoogleConf extends Configuration[GoogleConf]("google") {
  override def buildConfig(c: Config): GoogleConf = {
    GoogleConf(Try(c.getString("key")).getOrElse(""))
  }
}
