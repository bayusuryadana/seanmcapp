package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}
import config.Configuration

import scala.util.Try

case class AWSConf(access: String, secret: String, region: String, bucket: String)

object AWSConf extends Configuration[AWSConf] {

  override val prefix = "aws"

  def apply(): AWSConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): AWSConf = {
    AWSConf(
      Try(c.getString("access")).getOrElse(""),
      Try(c.getString("secret")).getOrElse(""),
      Try(c.getString("region")).getOrElse(""),
      Try(c.getString("bucket")).getOrElse("")
    )
  }
}
