package com.seanmcapp.config

import com.typesafe.config.{Config, ConfigFactory}
import config.Configuration

import scala.util.Try

case class SchedulerConf(amartha: Seq[Long], igrow: Seq[Long])

object SchedulerConf extends Configuration[SchedulerConf] {

  override val prefix: String = "scheduler"

  def apply(): SchedulerConf = apply(ConfigFactory.load)

  override def fromSubConfig(c: Config): SchedulerConf = {
    SchedulerConf(Try(c.getLongList("amartha").asInstanceOf[Seq[Long]])
      .getOrElse(Seq.empty[Long]),
      Try(c.getLongList("igrow").asInstanceOf[Seq[Long]]).getOrElse(Seq.empty[Long])
    )
  }
}
