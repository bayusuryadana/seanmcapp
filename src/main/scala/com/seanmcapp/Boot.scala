package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.Try

object Boot extends App with ScheduleManager {

  implicit val system = ActorSystem("seanmcapp")
  implicit val _ec = system.dispatcher
  implicit val _mat = ActorMaterializer()

  lazy val route = new Route().routePath

  val serverBinding = start(Try(System.getenv("PORT").toInt).toOption.getOrElse(8000))

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    runScheduler
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
