package com.seanmcapp.util.requestbuilder

import scalaj.http.{Http, MultiPart}

import scala.util.{Failure, Success, Try}

trait HttpRequestBuilder {

  def sendGetRequest(url: String): String

  def sendRequest(url: String, postData: Option[String] = None, headers: Option[Map[String, String]] = None,
                  timeout: Option[(Int, Int)] = None): String

  def sendMultipartRequest(url: String, parts: MultiPart, params: Option[Map[String, String]] = None,
                           headers: Option[Map[String, String]] = None,
                           timeout: Option[(Int, Int)] = None): String
}

object HttpRequestBuilderImpl extends HttpRequestBuilder {

  def sendGetRequest(url: String): String = {
    Try(Http(url).asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
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

  def sendMultipartRequest(url: String, parts: MultiPart,
                           params: Option[Map[String, String]] = None,
                           headers: Option[Map[String, String]] = None,
                           timeout: Option[(Int, Int)] = None): String = {
    val httpUrl = Http(url)
    val httpData = params.map(p => httpUrl.params(p).postMulti(parts)).getOrElse(httpUrl)
    val httpHeaders = headers.map(h => httpData.headers(h)).getOrElse(httpData)
    val httpTimeout = timeout.map(t => httpHeaders.timeout(t._1, t._2)).getOrElse(httpHeaders)

    Try(httpTimeout.asString.throwError.body) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }
}
