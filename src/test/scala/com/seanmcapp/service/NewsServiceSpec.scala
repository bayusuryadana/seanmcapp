package com.seanmcapp.service

import com.seanmcapp.external.{HttpRequestClientImpl, NewsClient, TelegramClient}
import org.mockito.Mockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NewsServiceSpec extends AnyWordSpec with Matchers {

  "should return all of the news" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val http = HttpRequestClientImpl
    val newsClient = new NewsClient(http)
    val newsService = new NewsService(newsClient, telegramClient)
    val result = newsService.run
    println(result)
    result
  }

}
