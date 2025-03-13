package com.seanmcapp.service

import com.seanmcapp.repository.PeopleRepo

import scala.concurrent.Await
import scala.concurrent.duration.Duration

// $COVERAGE-OFF$
class WarmupDBService(peopleRepo: PeopleRepo) extends ScheduledTask {

  override def run: Any = {
    println("=== warmup database ===")
    val res = Await.result(peopleRepo.get(getCurrentTime.getDayOfMonth, getCurrentTime.getMonthOfYear), Duration.Inf)
    println("warmup result: " + res)
  }

}
