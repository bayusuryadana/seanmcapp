package com.seanmcapp.service

import com.seanmcapp.external.TelegramClient
import com.seanmcapp.repository.birthday.PeopleRepo

import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
class BirthdayService(peopleRepo: PeopleRepo, telegramClient: TelegramClient) extends ScheduledTask {

  override def run: Any = {
    for {
      people <- peopleRepo.get(getCurrentTime.getDayOfMonth, getCurrentTime.getMonthOfYear)
    } yield {
      people.map { person =>
        val result = s"Today is ${person.name}'s birthday !!"
        telegramClient.sendMessage(274852283, result)
      }
    }
  }

}
