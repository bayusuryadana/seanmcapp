package com.seanmcapp.client

import com.seanmcapp.util.HttpConf
import com.seanmcapp.util.ExceptionHandler
import io.circe.generic.AutoDerivation
import scalaj.http.{BaseHttp, HttpOptions, HttpRequest, HttpResponse, MultiPart, StringBodyConnectFunc}
import io.circe.syntax._

import scala.util.{Failure, Success, Try}

// $COVERAGE-OFF$
case class ParamMap(params: Map[String, String])

case class HeaderMap(headers: Map[String, String])

trait HttpRequestClient {

  def sendGetRequest(url: String, headers: Option[HeaderMap] = None): String

  def sendRequest(url: String, params: Option[ParamMap] = None, postData: Option[String] = None,
                  headers: Option[HeaderMap] = None, multiPart: Option[MultiPart] = None,
                  postForm: Option[Seq[(String, String)]] = None): HttpResponse[String]

}

object HttpRequestClientImpl extends HttpRequestClient {

  def sendGetRequest(url: String, headers: Option[HeaderMap] = None): String =
    Http(url).add(headers).send().body

  def sendRequest(url: String,
                  params: Option[ParamMap] = None,
                  postData: Option[String] = None,
                  headers: Option[HeaderMap] = None,
                  multiPart: Option[MultiPart] = None,
                  postForm: Option[Seq[(String, String)]] = None
                 ): HttpResponse[String] = {
    Http(url).add(params).add(postData).add(headers).add(multiPart).add(postForm).send()
  }

  implicit class HttpRequestUtil(httpRequest: HttpRequest) extends AutoDerivation {
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

    def send(): HttpResponse[String] = {
      Try(httpRequest.asString.throwError) match {
        case Success(res) => res
        case Failure(e) =>
          println(s"[ERROR] ${e.getMessage}")
          throw new HttpRequestClientException(httpRequest, e)
      }
    }
  }

}

object Http extends BaseHttp {

  private[client] val httpConf = HttpConf()

  override def apply(url: String): HttpRequest = {
    val httpOptions = Seq(
      HttpOptions.connTimeout(httpConf.connTimeout),
      HttpOptions.readTimeout(httpConf.readTimeout),
      HttpOptions.followRedirects(httpConf.followRedirects)
    )
    super.apply(url).options(httpOptions)
  }
}

class HttpRequestClientException(httpRequest: HttpRequest, t: Throwable) extends ExceptionHandler(t) {
  override val processedMessage: String = {
    val data = httpRequest.connectFunc match {
      case sb: StringBodyConnectFunc => sb.data
      case _ => ""
    }
    
    s"""${this.getMessage}
       |destination-url: ${httpRequest.url}
       |method: ${httpRequest.method}
       |data: $data
       |params: ${httpRequest.params.toMap.asJson.toString()}
       |headers: ${httpRequest.headers.toMap.asJson.toString()}
       |""".stripMargin
  }
}