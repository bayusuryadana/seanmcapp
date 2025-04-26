package com.seanmcapp.util

import org.apache.pekko.http.scaladsl.model.{HttpResponse, StatusCode}
import org.apache.pekko.http.scaladsl.model.StatusCodes.InternalServerError
import org.apache.pekko.http.scaladsl.server
import org.apache.pekko.http.scaladsl.server.Directives.{complete, extractUri}
import org.apache.pekko.http.scaladsl.server.{ExceptionHandler, Route}

class ExceptionHandler(t: Throwable) extends Exception(t) {
  
  val processedMessage: String = this.getMessage
  val responseCode: StatusCode = InternalServerError
  
  private val errorAnjing = "\n+----------------+\n| ERROR ANJING ! |\n+----------------+\n\n"

  def doPrintAndComplete(): Route = {
    extractUri { uri =>
      def template(m: String): String = s"${errorAnjing}endpoint: $uri\nmessage: $m"

      doPrint(Some(template(processedMessage)))

      val browserMessage = template(this.getMessage)
      complete(HttpResponse(responseCode, entity = browserMessage))
    }
  }
  
  def doPrint(customMessage: Option[String] = None): Unit = {
    if (customMessage.isEmpty) println(customMessage) else println(s"${errorAnjing}message: $processedMessage")
    println("---------- STACK TRACE ----------")
    this.printStackTrace()
  }
}

object APIExceptionHandler {
  
  def apply(): server.ExceptionHandler = {
    ExceptionHandler {
      case eh: ExceptionHandler => eh.doPrintAndComplete()
      case e: Exception => new ExceptionHandler(e).doPrintAndComplete()
    }
  }

}
