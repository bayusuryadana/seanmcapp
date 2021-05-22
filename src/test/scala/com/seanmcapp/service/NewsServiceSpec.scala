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
      "Rumitnya Memenjarakan Anak Pejabat yang Memerkosa & Menjual Bocah",
      "KH Said Aqil ke Listyo Sigit: Yang Khotbah Jumat Katai Jokowi Kenapa Dibiarkan kumparanNEWS",
      "How Redditors made investors & Wall Street hedge funds lose S$8 billion, explained",
      "More than 113,000 people in Singapore receive first dose of COVID-19 vaccine: MOH",
      "COVID-19 deaths in Latin America surpass 1 mln as outbreak worsens"
    )
    val expectedUrl = List(
      "https://tirto.id/rumitnya-memenjarakan-anak-pejabat-yang-memerkosa-menjual-bocah-ggcG?utm_source=Tirtoid&utm_medium=Popular",
      "https://kumparan.com/kumparannews/kh-said-aqil-ke-listyo-sigit-yang-khotbah-jumat-katai-jokowi-kenapa-dibiarkan-1v4EoB66JdO",
      "https://mothership.sg/2021/01/gamestop-hedge-fund-shorting-explainer/",
      "https://www.channelnewsasia.com/news/singapore/covid-19-vaccine-113-000-receive-first-dose-moh-14062956",
      "https://www.reuters.com/world/americas/covid-19-deaths-latin-america-set-surpass-1-mln-outbreak-worsens-2021-05-21/",
    )
    val result = newsService.run
    result.map(_.title) shouldBe expectedTitles
    result.map(_.url) shouldBe expectedUrl
  }

}
