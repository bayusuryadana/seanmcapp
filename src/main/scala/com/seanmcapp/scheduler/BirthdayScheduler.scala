package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.external.TelegramClient
import com.seanmcapp.repository.birthday.PeopleRepo
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

// $COVERAGE-OFF$
class BirthdayScheduler(startTime: Int, interval: FiniteDuration, peopleRepo: PeopleRepo, override val http: HttpRequestBuilder)
                       (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with TelegramClient {

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
// $COVERAGE-ON$
