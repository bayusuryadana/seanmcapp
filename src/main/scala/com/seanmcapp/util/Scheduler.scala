package com.seanmcapp.util

import java.util.concurrent.TimeUnit

import com.seanmcapp.startup.Boot.system

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration

object Scheduler {

  def init()(implicit _ec: ExecutionContextExecutor): Unit = {
    val scheduler = system.scheduler
    val task = new Runnable { def run() { println("tot") } }
    scheduler.schedule(
      initialDelay = Duration(3, TimeUnit.SECONDS),
      interval = Duration(3, TimeUnit.SECONDS),
      runnable = task)
  }

}
