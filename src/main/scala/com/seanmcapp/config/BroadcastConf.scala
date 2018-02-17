package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}
import config.Configuration

import scala.util.Try

case class BroadcastConf(key: String)

object BroadcastConf extends Configuration[BroadcastConf] {

  override val prefix = "broadcast"

  def apply(): BroadcastConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): BroadcastConf = {
    BroadcastConf(
      Try(c.getString("key")).getOrElse("")
    )
  }

}
