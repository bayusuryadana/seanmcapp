package com.seanmcapp.util

import org.apache.pekko.http.scaladsl.model.HttpMethods._
import org.apache.pekko.http.scaladsl.model.headers._
import org.apache.pekko.http.scaladsl.model.{HttpResponse, StatusCodes}
import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.directives.RouteDirectives.complete
import org.apache.pekko.http.scaladsl.server.{Directive0, Route}

// $COVERAGE-OFF$
trait CORSHandler {
  private val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization",
      "Content-Type", "X-Requested-With", "secretkey")
  )

  private def addAccessControlHeaders(): Directive0 = {
    respondWithHeaders(corsResponseHeaders)
  }

  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).
      withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  def corsHandler(r: Route): Route = addAccessControlHeaders() {
    preflightRequestHandler ~ r
  }

}
