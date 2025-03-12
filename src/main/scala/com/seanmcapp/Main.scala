package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import com.seanmcapp.util.APIExceptionHandler

import scala.concurrent.{ExecutionContextExecutor, Future}

object Main extends App with CORSHandler {

  implicit val system: ActorSystem = ActorSystem("seanmcapp")
  implicit val _ec: ExecutionContextExecutor = system.dispatcher

  private lazy val setup = new Setup

  private val serverBinding = start(8080)

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    println(s"running schedule job")
    val runningJob = setup.scheduleList.map(_.start)
    system.registerOnTermination(runningJob.map(_.cancel()))

    println(s"Server is started on port $port")
    Http().newServerAt("0.0.0.0", port).bind(
      corsHandler(handleExceptions(APIExceptionHandler.apply()){ setup.route })
    )
  }

  private def stop(bindingFut: Future[ServerBinding]): Unit = {
    bindingFut.flatMap(_.unbind()).onComplete { _ =>
      println("Shutting down..")
      system.terminate()
    }
  }

}
