package com.seanmcapp.fetcher

import com.seanmcapp.util.requestbuilder.{HeaderMap, ParamMap}
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.parser._
import scalaj.http.MultiPart
import sttp.client._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HttpRequestClient {

  def sendGetRequest(url: String): Future[Either[String, String]] = {
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
