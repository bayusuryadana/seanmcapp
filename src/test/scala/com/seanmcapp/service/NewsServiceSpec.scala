package com.seanmcapp.service

import com.seanmcapp.client.{CNA, HttpRequestClient, Mothership, NewsObject, TelegramClient, Tirtol}
import com.seanmcapp.client.NewsObject.{NewsTitle, NewsUrl}
import org.jsoup.nodes.Document
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class NewsServiceSpec extends AnyWordSpec with Matchers {

  def mockNewsObject(newsObject: NewsObject): NewsObject = {
    new NewsObject {
      val name = newsObject.name
      val url = newsObject.name.toLowerCase
      val flag = Array()

      def parser(d: Document): (NewsTitle, NewsUrl) = newsObject.parser(d)
    }
  }

  "Scheduler" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val httpClient = Mockito.mock(classOf[HttpRequestClient])
    val newsListMock = List(
      mockNewsObject(Tirtol), mockNewsObject(Mothership), mockNewsObject(CNA)
    )
    val newsService = new NewsService(httpClient, telegramClient) {
      override private[service] val newsList = newsListMock
    }
    val newsMockResponse = newsListMock.map(newsObject => newsObject.url -> Source.fromResource(s"news/${newsObject.url}.html").mkString).toMap
    newsMockResponse.map { case (key, value) =>
      when(httpClient.sendGetRequest(key)).thenReturn(value)
    }
    val expectedTitles = List(
      "Tragedi Kanjuruhan: Mengapa Hanya Kapolres Malang yang Dicopot?",
      "How Redditors made investors & Wall Street hedge funds lose S$8 billion, explained",
      "117 people given lower dose of COVID-19 vaccine due to error at Bukit Merah Polyclinic"
    )
    val expectedUrl = List(
      "https://tirto.id/tragedi-kanjuruhan-mengapa-hanya-kapolres-malang-yang-dicopot-gwXQ?utm_source=Tirtoid&utm_medium=Popular",
      "https://mothership.sg/2021/01/gamestop-hedge-fund-shorting-explainer/",
      "https://www.channelnewsasia.com/singapore/bukit-merah-polyclinic-covid19-vaccine-lower-dose-singhealth-2265136"
    )
    
    val result = newsService.run
    result.map(_.title) shouldBe expectedTitles
    result.map(_.url) shouldBe expectedUrl
  }

}
