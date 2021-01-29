package com.seanmcapp.service

import com.seanmcapp.external.TelegramClient
import com.seanmcapp.repository.birthday.{People, PeopleRepo}

import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
class BirthdayService(peopleRepo: PeopleRepo, telegramClient: TelegramClient) extends ScheduledTask {

  override def run: Any = {
    val today = getCurrentTime
    val tmr = getCurrentTime.plusDays(1)
    val nextWeek = getCurrentTime.plusDays(7)
    for {
      people <- peopleRepo.get(today.getDayOfMonth, today.getMonthOfYear)
      people1 <- peopleRepo.get(tmr.getDayOfMonth, tmr.getMonthOfYear)
      people7 <- peopleRepo.get(nextWeek.getDayOfMonth, nextWeek.getMonthOfYear)
    } yield {
      people.foreach { p => sendMessage(p, 0) }
      people1.foreach { p => sendMessage(p, 1) }
      people7.foreach { p => sendMessage(p, 7) }
    }
  }

  private def sendMessage(person: People, numOfDays: Int): Unit = {
    val dayWord = numOfDays match {
      case 0 => "Today"
      case 1 => "Tomorrow"
      case 7 => "Next week"
    }
    val result = s"$dayWord is ${person.name}'s birthday !!"
    telegramClient.sendMessage(274852283, result)
  }

}
