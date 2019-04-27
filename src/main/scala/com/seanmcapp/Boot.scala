package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.DateTime
import akka.stream.ActorMaterializer
import com.seanmcapp.repository.birthday.PeopleRepoImpl

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.Try

object Boot extends App {

  implicit val system = ActorSystem("seanmcapp")
  implicit val _ec = system.dispatcher
  implicit val _mat = ActorMaterializer()

  lazy val route = new Route().routePath

  val serverBinding = start(Try(System.getenv("PORT").toInt).toOption.getOrElse(9000))

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    // warmup DB
    val scheduler = system.scheduler
    scheduler.scheduleOnce(Duration(0, TimeUnit.SECONDS))(warmup)
    scheduler.scheduleOnce(Duration(10, TimeUnit.SECONDS))(warmup)

    println(s"Server is started on port $port")
    Http().bindAndHandle(route, "0.0.0.0", port)
  }

  def stop(bindingFut: Future[ServerBinding]): Unit = {
    bindingFut.flatMap(_.unbind()).onComplete { _ =>
      println("Shutting down..")
      system.terminate()
    }
  }

  private def warmup: Unit = {
    println("== warmup database ==")
    val now = DateTime.now
    val res = Await.result(PeopleRepoImpl.get(now.day, now.month), Duration.Inf)
    println("warmup result: " + res)
  }
}
