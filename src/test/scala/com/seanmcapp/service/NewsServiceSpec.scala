package com.seanmcapp.service

import com.seanmcapp.external.{NewsClient, TelegramClient}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class NewsServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val newsClient = Mockito.mock(classOf[NewsClient])
    val newsService = new NewsService(newsClient, telegramClient)
    val mockResponse = NewsConstant.mapping.keys.map(key => key -> Source.fromResource(s"news/$key.html").mkString).toMap
    when(newsClient.getNews).thenReturn(mockResponse)
    val expectedTitles = List(
      "The Medium Menghadirkan Kengerian Lewat Cara yang Efektif",
      "KH Said Aqil ke Listyo Sigit: Yang Khotbah Jumat Katai Jokowi Kenapa Dibiarkan kumparanNEWS",
      "How Redditors made investors & Wall Street hedge funds lose S$8 billion, explained",
      "117 people given lower dose of COVID-19 vaccine due to error at Bukit Merah Polyclinic"
    )
    val expectedUrl = List(
      "https://tirto.id/the-medium-menghadirkan-kengerian-lewat-cara-yang-efektif-gkDj",
      "https://kumparan.com/kumparannews/kh-said-aqil-ke-listyo-sigit-yang-khotbah-jumat-katai-jokowi-kenapa-dibiarkan-1v4EoB66JdO",
      "https://mothership.sg/2021/01/gamestop-hedge-fund-shorting-explainer/",
      "https://www.channelnewsasia.com/singapore/bukit-merah-polyclinic-covid19-vaccine-lower-dose-singhealth-2265136"
    )
    val result = newsService.run
    result.map(_.title) shouldBe expectedTitles
    result.map(_.url) shouldBe expectedUrl
  }

}
