package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.repository.dota.PlayerRepoImpl
import com.seanmcapp.scheduler._
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, HttpRequestBuilderImpl}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

trait ScheduleManager {

  implicit val system: ActorSystem
  implicit val _ec: ExecutionContext
  implicit val _mat: Materializer

  def runScheduler: List[Cancellable] = {
    val everyDay = Duration(1, TimeUnit.DAYS)

    val peopleRepo = PeopleRepoImpl
    val playerRepo = PlayerRepoImpl
    val http = HttpRequestBuilderImpl

    val scheduleList = List(
      new WarmupDBScheduler(0, peopleRepo),
      new WarmupDBScheduler(10, peopleRepo),
      new DotaMetadataScheduler(3, everyDay, playerRepo, http),

      new BirthdayScheduler(6, everyDay, peopleRepo, http),
      new IGrowScheduler(6, everyDay, http),

      new AirVisualScheduler(8, everyDay, http),
      new AirVisualScheduler(17, everyDay, http)
    ).map(_.run)

    system.registerOnTermination(scheduleList.map(_.cancel()))
    scheduleList
  }

}
