package com.seanmcapp.config
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class AmarthaConf(username: String, password: String)

object AmarthaConf extends Configuration[AmarthaConf] {

  override val prefix: String = "amartha"

  def apply(): AmarthaConf = super.apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): AmarthaConf = {
    AmarthaConf(
      Try(c.getString("username")).getOrElse(""),
      Try(c.getString("password")).getOrElse("")
    )
  }
}
