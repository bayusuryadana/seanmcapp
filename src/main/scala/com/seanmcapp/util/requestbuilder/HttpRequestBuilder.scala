package com.seanmcapp.util.requestbuilder

import scalaj.http.{Http, HttpRequest}

import scala.util.{Failure, Success, Try}

trait HttpRequestBuilder {
  def sendRequest(url: String): String
  def sendRequest(httpRequest: HttpRequest): String
  def sendRequest(url: String, headers: Map[String, String]): String
}

object HttpRequestBuilderImpl extends HttpRequestBuilder {

  def sendRequest(url: String): String = {
    val httpRequest = Http(url)
    sendRequest(httpRequest)
  }

  def sendRequest(httpRequest: HttpRequest): String = {
    Try(httpRequest.asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }

  def sendRequest(url: String, headers: Map[String, String]): String = {
    val httpRequest = Http(url).headers(headers)
    sendRequest(httpRequest)
  }
}
