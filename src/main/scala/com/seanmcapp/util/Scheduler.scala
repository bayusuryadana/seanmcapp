package com.seanmcapp.util

import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.DateTime
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.startup.Boot.system
import com.seanmcapp.startup.Injection
import com.seanmcapp.util.requestbuilder.TelegramRequest

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.concurrent.duration.Duration

import scala.concurrent.ExecutionContext.Implicits.global

object Scheduler extends TelegramRequest with Injection {

  val peopleRepo = PeopleRepoImpl

  def init()(implicit _ec: ExecutionContextExecutor): Unit = {
    val scheduler = system.scheduler

    // warmup DB
    scheduler.scheduleOnce(Duration(0, TimeUnit.SECONDS))(warmup)
    scheduler.scheduleOnce(Duration(10, TimeUnit.SECONDS))(warmup)
    //scheduler.schedule(Duration(30, TimeUnit.SECONDS), Duration(2, TimeUnit.HOURS))(birthdayCheck)
  }

  private def warmup: Unit = {
    println("== warmup database ==")
    val now = DateTime.now
    val res = Await.result(peopleRepo.get(now.day, now.month), Duration.Inf)
    println("warmup result: " + res)
  }


  // TODO: need another way to do scheduler
  private def birthdayCheck: Unit = {

    val now = DateTime.now // akka datetime doesn't support timezones, so this is UTC
    if (now.hour >= 5 && now.hour < 7) {
      println("---> Checking birthday")
      for{
        people <- peopleRepo.get(now.day, now.month)
      } yield {
        println("Today's birthday: " + people)
        people.map(person => sendMessage(274852283, "Today is " + person.name + " birthday !!"))
      }
    }

  }

}
