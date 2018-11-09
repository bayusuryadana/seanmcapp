package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}
import config.Configuration

import scala.util.Try

case class DriveConf(url: String)

object DriveConf extends Configuration[DriveConf] {

  override val prefix: String = "drive"

  def apply(): DriveConf = apply(ConfigFactory.load())

  override def fromSubConfig(c: Config): DriveConf = {
    DriveConf(Try(c.getString("url")).getOrElse(""))
  }
}
