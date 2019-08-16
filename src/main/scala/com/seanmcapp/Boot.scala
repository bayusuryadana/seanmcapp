package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.seanmcapp.scheduler.{AirVisualScheduler, AmarthaScheduler, BirthdayScheduler, DotaMetadataFetcherScheduler, IGrowScheduler, Scheduler, WarmupDBScheduler}

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

object Boot extends App {

  implicit val system = ActorSystem("seanmcapp")
  implicit val _ec = system.dispatcher
  implicit val _mat = ActorMaterializer()

  lazy val route = new Route().routePath

  lazy val schedulerList = runScheduler

  val serverBinding = start(Try(System.getenv("PORT").toInt).toOption.getOrElse(9000))

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    schedulerList
    println(s"Server is started on port $port")
    Http().bindAndHandle(route, "0.0.0.0", port)
  }

  def stop(bindingFut: Future[ServerBinding]): Unit = {
    bindingFut.flatMap(_.unbind()).onComplete { _ =>
      println("Shutting down..")
      system.terminate()
    }
  }

  private def runScheduler: List[Cancellable] = {
    val everyDay = Duration(1, TimeUnit.DAYS)

    val scheduleList: List[Scheduler] = List(
      new WarmupDBScheduler(0),
      new WarmupDBScheduler(10),
      new DotaMetadataFetcherScheduler(3, everyDay),

      new BirthdayScheduler(6, everyDay),
      new IGrowScheduler(6, everyDay),
      new AmarthaScheduler(12, Some(everyDay)),

      new AirVisualScheduler(8, everyDay),
      new AirVisualScheduler(17, everyDay)
    )

    system.registerOnTermination(schedulerList.map(_.cancel()))
    scheduleList.map(_.run)
  }
}
