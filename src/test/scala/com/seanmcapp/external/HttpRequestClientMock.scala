package com.seanmcapp.external

import scalaj.http.MultiPart

class HttpRequestClientMock(responseMap: Map[String, String]) extends HttpRequestClient {

  override def sendGetRequest(url: String): String = {
    responseMap.getOrElse(url, "")
  }

  override def sendRequest(url: String,
                           params: Option[ParamMap] = None,
                           postData: Option[String] = None,
                           headers: Option[HeaderMap] = None,
                           multiPart: Option[MultiPart] = None
                          ): String = {
    responseMap.getOrElse(url, "")
  }

}
