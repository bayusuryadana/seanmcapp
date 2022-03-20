package com.seanmcapp.service

import com.seanmcapp.external.{AirVisualClient, NewsClient, TelegramClient}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters._
import scala.util.Try

case class NewsResult(title: String, url: String, flag: Array[Int])

class NewsService(newsClient: NewsClient, airVisualClient: AirVisualClient, telegramClient: TelegramClient) extends ScheduledTask {

  private val AirGood = Array(0x1F340)
  private val AirModerate = Array(0x1F60E)
  private val AirSensitive = Array(0x1F630)
  private val AirUnhealthy = Array(0x1F637)
  
  override def run: (List[NewsResult], String) = {
    // news
    val newsResponses = newsClient.getNews
    val newsResults = newsResponses.toList.flatMap { case (key, response) =>
      val newsObject = NewsConstant.mapping(key)
      Try(newsObject.parser(Jsoup.parse(response))).toOption.map(res => (newsObject.order, res))
    }.sortBy(_._1).map(_._2)

    // AQI
    val cityResults = airVisualClient.getCityResults
    val aqiMessage = cityResults.foldLeft("kondisi udara saat ini:") { (res, row) =>
      val city = row._1
      val aqius = row._2
      val appendString = "\n" + city.city + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
      res + appendString
    }

    val newsInitMessage = s"Awali harimu dengan berita ${new String(Array(0x1f4f0),0,1)} dari **Seanmctoday** by @seanmcbot\n\n"
    val newsMessage = newsResults.zipWithIndex.foldLeft(newsInitMessage) { (message, res) =>
      message + s"${res._2+1}. [${res._1.title}](${res._1.url}) ${new String(res._1.flag, 0, res._1.flag.length)}\n\n"
    }
    
    val combinedMessage = newsMessage + "\n\n" + aqiMessage
    telegramClient.sendMessage(-1001359004262L, combinedMessage)

    (newsResults, aqiMessage)
  }

  private def getEmojiFromAqi(aqi: Int): String = {
    aqi match {
      case _ if aqi <= 50 => new String(AirGood, 0, AirGood.length)
      case _ if aqi > 50 & aqi <= 100 => new String(AirModerate, 0, AirModerate.length)
      case _ if aqi > 100 & aqi <= 150 => new String(AirSensitive, 0, AirSensitive.length)
      case _ if aqi > 150 => new String(AirUnhealthy, 0, AirUnhealthy.length)
    }
  }

}

case class NewsObject(order: Int, url: String, parser: Document => NewsResult)

object NewsConstant {
  val mapping = Map(
    "tirto" -> NewsObject(1, "https://tirto.id", tirtoParser),
    "kumparan" -> NewsObject(2, "https://kumparan.com/trending", kumparanParser),
    "mothership" -> NewsObject(3, "https://mothership.sg", mothershipParser),
    "cna" -> NewsObject(4, "https://www.channelnewsasia.com/news/singapore", cnaParser),
//    "reuters" -> NewsObject(5, "https://www.reuters.com", reutersParser),
  )

  private def cnaParser(d: Document): NewsResult = {
    val tag = d.selectFirst(".card-object h3")
    NewsResult(
      tag.text(),
      s"https://www.channelnewsasia.com${tag.selectFirst("a").attr("href")}",
      Array(0x1f1f8, 0x1f1ec)
    )
  }

  private def kumparanParser(d: Document): NewsResult = {
    val tag = d.selectFirst("div[data-qa-id='news-item']")
    NewsResult(
      tag.selectFirst("a").text(),
      s"https://kumparan.com${tag.selectFirst("a").attr("href")}",
      Array(0x1f1ee, 0x1f1e9)
    )
  }

  private def mothershipParser(d: Document): NewsResult = {
    val tag = d.selectFirst(".main-item > .top-story")
    NewsResult(
      tag.selectFirst("h1").text(),
      tag.selectFirst("a").attr("href"),
      Array(0x1f1f8, 0x1f1ec)
    )
  }

  private def reutersParser(d: Document): NewsResult = {
    val tag = d.selectFirst(".StaticMediaMaximizer__hero___3tmwgq")
    NewsResult(
      tag.selectFirst(".MediaStoryCard__header___qimiYl a").text(),
      s"https://www.reuters.com${tag.selectFirst("a").attr("href")}",
      Array(0x1f30f)
    )
  }

  private def tirtoParser(d: Document): NewsResult = {
    // TODO: check whether the title matches 'POPULER', instead of hard code the index 5
    val tag = d.select(".welcome-title").asScala.toList(5).parent.parent.parent.parent.selectFirst(".mb-3 a")
    NewsResult(tag.text(), s"${tag.attr("href")}", Array(0x1f1ee, 0x1f1e9))
  }

}
