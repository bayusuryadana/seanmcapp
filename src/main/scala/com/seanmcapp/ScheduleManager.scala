package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.repository.dota.{HeroAttributeRepoImpl, HeroRepoImpl, PlayerRepoImpl}
import com.seanmcapp.scheduler._
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

trait ScheduleManager extends Injection {

  implicit val system: ActorSystem
  implicit val _ec: ExecutionContext
  implicit val _mat: Materializer

  def runScheduler: List[Cancellable] = {
    val everyDay = Duration(1, TimeUnit.DAYS)
    val everyHour = Duration(1, TimeUnit.HOURS)

    val peopleRepo = PeopleRepoImpl
    val playerRepo = PlayerRepoImpl
    val heroRepo = HeroRepoImpl
    val heroAttribRepo = HeroAttributeRepoImpl
    val http = HttpRequestBuilderImpl

    val scheduleList = List(
      new WarmupDBScheduler(0, peopleRepo),
      new WarmupDBScheduler(10, peopleRepo),
      new DotaMetadataScheduler(3, everyDay, playerRepo, heroRepo, heroAttribRepo, http),
      new BirthdayScheduler(6, everyDay, peopleRepo, http),
      new IGrowScheduler(6, everyDay, http),
      new AirVisualScheduler(8, everyDay, http),
      new AirVisualScheduler(17, everyDay, http),
      new NCovScheduler(20, everyDay, http),
      new DsdaJakartaScheduler(0, everyHour, http),
      new AmarthaScheduler(7, everyDay, amarthaAPI, http)
    ).map(_.start)

    system.registerOnTermination(scheduleList.map(_.cancel()))
    scheduleList
  }

}
