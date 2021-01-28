package com.seanmcapp.external

import com.seanmcapp.service.NewsConstant
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NewsClientSpec extends AnyWordSpec with Matchers {

  "getNews" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val newsClient = new NewsClient(http)
    val response = "<html></html>"
    when(http.sendGetRequest(any(), any())).thenReturn(response)
    val expected = NewsConstant.mapping.keys.map(key => key -> response).toMap
    newsClient.getNews shouldBe expected
  }

}
