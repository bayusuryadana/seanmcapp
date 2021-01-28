package com.seanmcapp.external

import com.seanmcapp.service.NewsConstant

class NewsClient(http: HttpRequestClient) {

  def getNews: Map[String, String] = NewsConstant.mapping.toList.map { case (key, value) =>
    key -> http.sendGetRequest(value.url)
  }.toMap

}
