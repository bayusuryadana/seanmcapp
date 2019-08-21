package com.seanmcapp.util.requestbuilder

import scalaj.http.Http

import scala.util.{Failure, Success, Try}

trait HttpRequestBuilder {
  def sendRequest(url: String): String
}

object HttpRequestBuilderImpl extends HttpRequestBuilder {
  def sendRequest(url: String): String = {
    Try(Http(url).asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }
}
