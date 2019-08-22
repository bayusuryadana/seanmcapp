package com.seanmcapp.util.requestbuilder

import scalaj.http.{Http, HttpRequest}

import scala.util.{Failure, Success, Try}

trait HttpRequestBuilder {
  def sendRequest(url: String): String
  def sendRequest(httpRequest: HttpRequest): String
  def sendRequest(url: String, headers: Map[String, String]): String
  def sendRequest(url: String, postData: Option[String] = None, headers: Option[Map[String, String]] = None,
                  timeout: Option[(Int, Int)] = None): String
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

  def sendRequest(url: String, postData: Option[String] = None, headers: Option[Map[String, String]] = None,
                  timeout: Option[(Int, Int)] = None): String = {
    val httpUrl = Http(url)
    val httpPostData = postData.map(pd => httpUrl.postData(pd)).getOrElse(httpUrl)
    val httpHeaders = headers.map(h => httpPostData.headers(h)).getOrElse(httpPostData)
    val httpTimeout = timeout.map(t => httpHeaders.timeout(t._1, t._2)).getOrElse(httpHeaders)

    Try(httpTimeout.asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }

}
