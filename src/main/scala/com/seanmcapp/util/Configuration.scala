package com.seanmcapp.util

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

// $COVERAGE-OFF$
abstract class Configuration[T](prefix: String) {

  def apply(): T = {
    try {
      val config = ConfigFactory.load.getConfig(prefix)
      buildConfig(config)
    } catch {
      case e: Exception => throw new Exception("Configuration are not exists/incorrect", e)
    }
  }

  def buildConfig(c: Config): T

}

case class AppsConf(password: String, secretKey: String)
object AppsConf extends Configuration[AppsConf]("application") {
  override def buildConfig(c: Config): AppsConf = {
    println(c)
    AppsConf(
      c.getString("password"),
      c.getString("secret-key")
    )
  }
}

case class DatabaseConf(name: String, host: String, user: String, pass: String)
object DatabaseConf extends Configuration[DatabaseConf]("database") {
  override def buildConfig(c: Config): DatabaseConf = {
    DatabaseConf(
      c.getString("name"),
      c.getString("host"),
      c.getString("user"),
      c.getString("pass")
    )
  }
}

case class HttpConf(connTimeout: Int, readTimeout: Int, followRedirects: Boolean)
object HttpConf extends Configuration[HttpConf]("http") {
  override def buildConfig(c: Config): HttpConf = {
    HttpConf(
      c.getInt("conn-timeout"),
      c.getInt("read-timeout"),
      c.getBoolean("follow-redirects")
    )
  }
}

case class TelegramConf(endpoint: String, botname: String)
object TelegramConf extends Configuration[TelegramConf]("telegram") {
  override def buildConfig(c: Config): TelegramConf = {
    TelegramConf(
      c.getString("bot-endpoint"),
      c.getString("bot-name")
    )
  }
}
