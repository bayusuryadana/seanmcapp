package com.seanmcapp.startup

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.seanmcapp.util.Scheduler

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.util.Try

object Boot extends App {

  implicit val system = ActorSystem("seanmcapp")
  implicit val _ec = system.dispatcher
  implicit val _mat = ActorMaterializer()

  lazy val route = new Route().routePath

  val serverBinding = start(Try(System.getenv("PORT").toInt).toOption.getOrElse(8080))

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    Scheduler.init
    println(s"Server is started on port $port")
    Http().bindAndHandle(route, "0.0.0.0", port)
  }

  def stop(bindingFut: Future[ServerBinding]): Unit = {
    bindingFut.flatMap(_.unbind()).onComplete { _ =>
      println("Shutting down..")
      system.terminate()
    }
  }
}
