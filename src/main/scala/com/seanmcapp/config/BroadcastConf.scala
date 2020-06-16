package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class BroadcastConf(secretKey: String)

object BroadcastConf extends Configuration[BroadcastConf] {
  override val prefix: String = "broadcast"

  def apply(): BroadcastConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): BroadcastConf = {
    BroadcastConf(Try(c.getString("secret-key")).getOrElse(""))
  }
}