package com.seanmcapp.scheduler

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.repository.birthday.PeopleRepoImpl

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.{Duration, FiniteDuration}

class WarmupDBScheduler(startTime: Int)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, None) {

  private val peopleRepo = PeopleRepoImpl

  override def getStartTimeDuration(hour: Int): FiniteDuration = Duration(startTime, TimeUnit.SECONDS)

  override def task: Unit = {
    println("=== warmup database ===")
    val res = Await.result(peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear), Duration.Inf)
    println("warmup result: " + res)
  }

}
