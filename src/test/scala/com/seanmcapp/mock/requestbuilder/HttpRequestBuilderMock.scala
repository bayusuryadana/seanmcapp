package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.util.requestbuilder.HttpRequestBuilder

class HttpRequestBuilderMock(responseMap: Map[String, String]) extends HttpRequestBuilder {

  override def sendGetRequest(url: String): String = {
    responseMap.getOrElse(url, "")
  }

  override def sendRequest(url: String, postData: Option[String] = None, headers: Option[Map[String, String]] = None,
                  timeout: Option[(Int, Int)] = None): String = {
    "NOT YET IMPLEMENTED"
  }

}
