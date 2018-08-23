package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}
import config.Configuration

import scala.util.Try

case class MongoConf(connectionString: String)

object MongoConf extends Configuration[MongoConf] {

  override val prefix = "mongo"

  def apply(): MongoConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): MongoConf = {
    MongoConf(Try(c.getString("connection-string")).getOrElse(""))
  }
}
