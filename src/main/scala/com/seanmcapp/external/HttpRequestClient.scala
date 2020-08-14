package com.seanmcapp.external

import com.seanmcapp.util.requestbuilder.{HeaderMap, ParamMap}
import scalaj.http.MultiPart
import sttp.client._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
class HttpRequestClient {

  def sendRequest(url: String): Future[Either[String, String]] = {
    val request = basicRequest.get(uri"$url")
    implicit val backend: SttpBackend[Identity, Nothing, NothingT] = HttpURLConnectionBackend()
    Future(request.send()).map(_.body)
  }

//  def sendRequest[T](url: String,
//                  params: Option[ParamMap] = None,
//                  postData: Option[String] = None,
//                  headers: Option[HeaderMap] = None,
//                  multiPart: Option[MultiPart] = None
//                 ): Future[Either[String, T]] = ???

}
