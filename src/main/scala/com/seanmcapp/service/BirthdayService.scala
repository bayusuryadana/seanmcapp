package com.seanmcapp.service

import akka.http.scaladsl.model.DateTime
import com.seanmcapp.repository.birthday.PeopleRepo
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait BirthdayService extends TelegramRequestBuilder {

  val peopleRepo: PeopleRepo

  // cronjob from cron-job.org
  def check: Future[String] = {
    val now = DateTime.now // akka datetime doesn't support timezones, so this is UTC
    println("---> Checking birthday")
    for{
      people <- peopleRepo.get(now.day, now.month)
    } yield {
      val result = "Today's birthday: " + people.map(_.name + ",")
      people.map(person => sendMessage(274852283, "Today is " + person.name + " birthday !!"))
      result
    }
  }
}
