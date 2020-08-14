package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.external.TelegramClient
import com.seanmcapp.repository.birthday.PeopleRepo

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class BirthdayScheduler(startTime: Int, interval: FiniteDuration, peopleRepo: PeopleRepo, telegramClient: TelegramClient)
                       (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  override def task: Unit = {
    println("=== birthday check ===")
    for{
      people <- peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear)
    } yield {
      people.map { person =>
        val result = s"Today is ${person.name}'s birthday !!"
        println(result)
        telegramClient.sendMessage(274852283, result)
      }
    }
  }

}
