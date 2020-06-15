package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class StorageConf(access: String, secret: String, host: String, bucket: String)

object StorageConf extends Configuration[StorageConf] {

  override val prefix = "aws"

  def apply(): StorageConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): StorageConf = {
    StorageConf(
      Try(c.getString("access")).getOrElse(""),
      Try(c.getString("secret")).getOrElse(""),
      Try(c.getString("host")).getOrElse("http://localhost"),
      Try(c.getString("bucket")).getOrElse("")
    )
  }
}
