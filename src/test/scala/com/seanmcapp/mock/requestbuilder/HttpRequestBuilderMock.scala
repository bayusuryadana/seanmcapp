package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.util.requestbuilder.{HeaderMap, HttpRequestBuilder, ParamMap}
import scalaj.http.MultiPart

class HttpRequestBuilderMock(responseMap: Map[String, String]) extends HttpRequestBuilder {

  override def sendGetRequest(url: String): String = {
    responseMap.getOrElse(url, "")
  }

  override def sendRequest(url: String,
                           params: Option[ParamMap] = None,
                           postData: Option[String] = None,
                           headers: Option[HeaderMap] = None,
                           multiPart: Option[MultiPart] = None
                          ): String = {
    "NOT YET IMPLEMENTED"
  }

}
