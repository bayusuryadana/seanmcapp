package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}
import config.Configuration

import scala.util.Try

case class InstagramConf(username: String, password: String)

object InstagramConf extends Configuration[InstagramConf] {

  override val prefix = "instagram"

  def apply(): InstagramConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): InstagramConf = {
    InstagramConf(
      username = Try(c.getString("username")).getOrElse(""),
      password = Try(c.getString("password")).getOrElse("")
    )
  }
}
