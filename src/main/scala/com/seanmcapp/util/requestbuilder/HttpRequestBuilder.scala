package com.seanmcapp.util.requestbuilder

import scalaj.http.{Http, HttpRequest, MultiPart}

import scala.util.{Failure, Success, Try}

case class ParamMap(params: Map[String, String])

case class HeaderMap(headers: Map[String, String])

trait HttpRequestBuilder {

  def sendGetRequest(url: String): String

  def sendRequest(url: String,
                  params: Option[ParamMap] = None,
                  postData: Option[String] = None,
                  headers: Option[HeaderMap] = None,
                  multiPart: Option[MultiPart] = None
                 ): String

}

object HttpRequestBuilderImpl extends HttpRequestBuilder {

  def sendGetRequest(url: String): String = {
    Try(Http(url).asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }

  def sendRequest(url: String,
                  params: Option[ParamMap] = None,
                  postData: Option[String] = None,
                  headers: Option[HeaderMap] = None,
                  multiPart: Option[MultiPart] = None
                 ): String = {
    val httpRequest = Http(url).add(params).add(postData).add(headers).add(multiPart)
    Try(httpRequest.asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }

  implicit class IntWithTimes(httpRequest: HttpRequest) {
    def add(input: Option[_]): HttpRequest = {
      input match {
        case Some(param: ParamMap) => httpRequest.params(param.params)
        case Some(postData: String) => httpRequest.postData(postData)
        case Some(headers: HeaderMap) => httpRequest.headers(headers.headers)
        case Some(multiPart: MultiPart) => httpRequest.postMulti(multiPart)
        case _ => httpRequest
      }
    }
  }

}
