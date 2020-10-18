package com.seanmcapp.external

import com.seanmcapp.HttpConf
import scalaj.http.{BaseHttp, HttpOptions, HttpRequest, MultiPart}

import scala.util.{Failure, Success, Try}

// $COVERAGE-OFF$
case class ParamMap(params: Map[String, String])

case class HeaderMap(headers: Map[String, String])

trait HttpRequestClient {

  def sendGetRequest(url: String): String

  def sendRequest(url: String, params: Option[ParamMap] = None, postData: Option[String] = None,
                  headers: Option[HeaderMap] = None, multiPart: Option[MultiPart] = None,
                  postForm: Option[Seq[(String, String)]] = None): String

}

object HttpRequestClientImpl extends HttpRequestClient {

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
                  multiPart: Option[MultiPart] = None,
                  postForm: Option[Seq[(String, String)]] = None
                 ): String = {
    val httpRequest = Http(url).add(params).add(postData).add(headers).add(multiPart).add(postForm)
    Try(httpRequest.asString.throwError) match {
      case Success(res) =>
        println(res.headers)
        res.body
      case Failure(e) => throw new Exception(e)
    }
  }

  implicit class HttpRequestUtil(httpRequest: HttpRequest) {
    def add(input: Option[_]): HttpRequest = {
      input match {
        case Some(param: ParamMap) => httpRequest.params(param.params)
        case Some(headers: HeaderMap) => httpRequest.headers(headers.headers)
        case Some(postData: String) => httpRequest.postData(postData)
        case Some(postForm: Seq[(String, String)]) => httpRequest.postForm(postForm)
        case Some(multiPart: MultiPart) => httpRequest.postMulti(multiPart)
        case _ => httpRequest
      }
    }
  }

}

object Http extends BaseHttp {

  private[external] val httpConf = HttpConf()

  override def apply(url: String): HttpRequest = {
    val httpOptions = Seq(
      HttpOptions.connTimeout(httpConf.connTimeout),
      HttpOptions.readTimeout(httpConf.readTimeout),
      HttpOptions.followRedirects(httpConf.followRedirects)
    )
    super.apply(url).options(httpOptions)
  }
}