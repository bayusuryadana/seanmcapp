package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class AirvisualConf(key: String)

object AirvisualConf extends Configuration[AirvisualConf] {
  override val prefix: String = "airvisual"

  def apply(): AirvisualConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): AirvisualConf = {
    AirvisualConf(Try(c.getString("api-key")).getOrElse(""))
  }
}
