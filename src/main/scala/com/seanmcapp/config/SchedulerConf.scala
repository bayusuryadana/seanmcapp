package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

case class SchedulerConf(igrow: Seq[Long])

object SchedulerConf extends Configuration[SchedulerConf] {

  override val prefix: String = "scheduler"

  def apply(): SchedulerConf = super.apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): SchedulerConf = {
    SchedulerConf(
      Try(c.getString("igrow").split(",").map(_.toLong).toSeq).getOrElse(Seq.empty[Long])
    )
  }
}
