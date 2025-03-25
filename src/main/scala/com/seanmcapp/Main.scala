package com.seanmcapp

import com.seanmcapp.util.{APIExceptionHandler, CORSHandler}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.Http.ServerBinding
import org.apache.pekko.http.scaladsl.server.Directives.handleExceptions

import scala.concurrent.{ExecutionContextExecutor, Future}

object Main extends App with CORSHandler {

  implicit val system: ActorSystem = ActorSystem("seanmcapp")
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val serverBinding = start(8080)

  scala.sys.addShutdownHook {
    stop(serverBinding)
    println("Server stopped...")
  }

  def start(port: Int): Future[ServerBinding] = {
    val (route, scheduleList) = new Bootstrap().init()
    println(s"running schedule job")
    val runningJob = scheduleList.map(_.start)
    system.registerOnTermination(runningJob.map(_.cancel()))

    println(s"Server is started on port $port")
    Http().newServerAt("0.0.0.0", port).bind(
      corsHandler(handleExceptions(APIExceptionHandler.apply()){ route })
    )
  }

  private def stop(bindingFut: Future[ServerBinding]): Unit = {
    bindingFut.flatMap(_.unbind()).onComplete { _ =>
      println("Shutting down..")
      system.terminate()
    }
  }

}
