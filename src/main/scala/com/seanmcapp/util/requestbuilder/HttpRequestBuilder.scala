package com.seanmcapp.util.requestbuilder

import scalaj.http.Http

import scala.util.{Failure, Success, Try}

class HttpRequestBuilder {

  def sendRequest(url: String): String = {
    Try(Http(url).asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }
}
