package com.seanmcapp.util

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.DateTime
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.startup.Boot.system
import com.seanmcapp.startup.Injection

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

object WarmUp extends Injection {

  val peopleRepo = PeopleRepoImpl

  def init()(implicit _ec: ExecutionContextExecutor): Unit = {
    val scheduler = system.scheduler

    // warmup DB
    scheduler.scheduleOnce(Duration(0, TimeUnit.SECONDS))(warmup)
    scheduler.scheduleOnce(Duration(10, TimeUnit.SECONDS))(warmup)
  }

  private def warmup: Unit = {
    println("== warmup database ==")
    val now = DateTime.now
    val res = Await.result(peopleRepo.get(now.day, now.month), Duration.Inf)
    println("warmup result: " + res)
  }

}
