package config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

abstract class Configuration[T] {

  val prefix: String

  def apply(config: Config): T =
    fromSubConfig(Try(config getConfig prefix).getOrElse(ConfigFactory.empty()))

  def fromSubConfig(c: Config): T

}
