package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.seanmcapp.scheduler.{AirVisualScheduler, AmarthaScheduler, BirthdayScheduler, DotaMetadataFetcherScheduler, IGrowScheduler, WarmupDBScheduler}
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

trait ScheduleManager {

  implicit val system: ActorSystem
  implicit val _ec: ExecutionContext
  implicit val _mat: Materializer

  def runScheduler(): List[Cancellable] = {
    val everyDay = Duration(1, TimeUnit.DAYS)

    val http = new HttpRequestBuilder

    val scheduleList = List(
      new WarmupDBScheduler(0),
      new WarmupDBScheduler(10),
      new DotaMetadataFetcherScheduler(3, everyDay),

      new BirthdayScheduler(6, everyDay),
      new IGrowScheduler(6, everyDay, http),
      new AmarthaScheduler(12, Some(everyDay)),

      new AirVisualScheduler(8, everyDay),
      new AirVisualScheduler(17, everyDay)
    ).map(_.run)

    system.registerOnTermination(scheduleList.map(_.cancel()))
    scheduleList
  }

}
