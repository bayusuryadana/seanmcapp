package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding

import scala.concurrent.Future
import scala.util.Try

object Boot extends App {

  implicit val system = ActorSystem("seanmcapp")
  implicit val _ec = system.dispatcher

  lazy val setup = new Setup

  val serverBinding = start(Try(System.getenv("PORT").toInt).toOption.getOrElse(8000))

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    println(s"running schedule job")
    val runningJob = setup.scheduleList.map(_.start)
    system.registerOnTermination(runningJob.map(_.cancel()))

    println(s"Server is started on port $port")
    Http().bindAndHandle(setup.route, "0.0.0.0", port)
  }

  def stop(bindingFut: Future[ServerBinding]): Unit = {
    bindingFut.flatMap(_.unbind()).onComplete { _ =>
      println("Shutting down..")
      system.terminate()
    }
  }

}
