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

  override def sendMultipartRequest(url: String,
                                    parts: scalaj.http.MultiPart,
                                    params: Option[Map[String,String]],
                                    headers: Option[Map[String,String]],
                                    timeout: Option[(Int, Int)]): String = {
    "NOT YET IMPLEMENTED"
  }

}
