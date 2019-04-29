package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.DateTime
import com.seanmcapp.Boot.system
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.service.BirthdayService

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

object Scheduler extends BirthdayService {

  override val peopleRepo = PeopleRepoImpl

  def start(implicit ec: ExecutionContext): Unit = {
    val scheduler = system.scheduler
    scheduler.scheduleOnce(Duration(0, TimeUnit.SECONDS))(warmup)
    scheduler.scheduleOnce(Duration(10, TimeUnit.SECONDS))(warmup)
    scheduler.schedule(Duration(20, TimeUnit.SECONDS), Duration(1, TimeUnit.DAYS))(birthdayCheck)
  }

  private def warmup: Unit = {
    println("== warmup database ==")
    val now = DateTime.now
    val res = Await.result(peopleRepo.get(now.day, now.month), Duration.Inf)
    println("warmup result: " + res)
  }

  private def birthdayCheck: Unit = {
    println("== checking any birthday today ==")
    check
  }

}
