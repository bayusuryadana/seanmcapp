package com.seanmcapp.service

import com.seanmcapp.external.{AirVisualClient, AirvisualCity, NewsClient, TelegramClient}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class NewsServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val newsClient = Mockito.mock(classOf[NewsClient])
    val airVisualClient = Mockito.mock(classOf[AirVisualClient])
    val newsService = new NewsService(newsClient, airVisualClient, telegramClient)

    val airVisualMockResponse = Map(
      AirvisualCity("Indonesia", "Jakarta", "Jakarta") -> 30,
      AirvisualCity("Indonesia", "West Java", "Bekasi") -> 80,
      AirvisualCity("Indonesia", "West Java", "Depok") -> 130,
      AirvisualCity("Singapore", "Singapore", "Singapore") -> 180
    )
    when(airVisualClient.getCityResults).thenReturn(airVisualMockResponse)
    
    val newsMockResponse = NewsConstant.mapping.keys.map(key => key -> Source.fromResource(s"news/$key.html").mkString).toMap
    when(newsClient.getNews).thenReturn(newsMockResponse)
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
    val expectedAQI = s"""kondisi udara saat ini:
                      |Jakarta (AQI 30 🍀)
                      |Bekasi (AQI 80 😎)
                      |Depok (AQI 130 😰)
                      |Singapore (AQI 180 😷)""".stripMargin
    
    val result = newsService.run
    result._1.map(_.title) shouldBe expectedTitles
    result._1.map(_.url) shouldBe expectedUrl
    result._2 shouldBe expectedAQI
  }

}
