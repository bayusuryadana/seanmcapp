package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.repository.birthday.PeopleRepoImpl

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

class BirthdayScheduler(startTime: Int, interval: FiniteDuration)
                       (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val peopleRepo = PeopleRepoImpl

  override def task: Future[String] = {
    println("=== birthday check ===")
    for{
      people <- peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear)
    } yield {
      val result = "Today's birthday: " + people.map(_.name + ",")
      people.map { person =>
        sendMessage(274852283, "Today is " + person.name + "'s birthday !!")
      }
      result
    }
  }

}
