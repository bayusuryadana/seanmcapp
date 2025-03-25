package com.seanmcapp.external

import com.seanmcapp.client.{HeaderMap, HttpRequestClient, ParamMap}
import scalaj.http.{HttpResponse, MultiPart}

class HttpRequestClientMock(responseMap: Map[String, String]) extends HttpRequestClient {

  override def sendGetRequest(url: String, headers: Option[HeaderMap] = None): String = responseMap.getOrElse(url, "")

  override def sendRequest(url: String,
                           params: Option[ParamMap] = None,
                           postData: Option[String] = None,
                           headers: Option[HeaderMap] = None,
                           multiPart: Option[MultiPart] = None,
                           postForm: Option[Seq[(String, String)]] = None
                          ): HttpResponse[String] = {
    val emptyResponse = HttpResponse[String]("", 0, Map.empty[String, IndexedSeq[String]])
    responseMap.get(url).map(res => emptyResponse.copy(body = res)).getOrElse(emptyResponse)
  }

}
