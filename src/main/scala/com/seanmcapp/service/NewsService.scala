package com.seanmcapp.service

import com.seanmcapp.client.{CNA, Detik, HttpRequestClient, Kumparan, Mothership, NewsResult, Reuters, TelegramClient, Tirtol}
import com.seanmcapp.util.{ChatIdType, ChatIdTypes}
import org.jsoup.Jsoup

import scala.util.Try

class NewsService(httpClient: HttpRequestClient, telegramClient: TelegramClient) extends ScheduledTask {

  private[service] val newsList = List(Detik, Tirtol, Kumparan, Mothership, CNA, Reuters)

  override def run: List[NewsResult] = process(ChatIdTypes.Group)

  def process(chatIdType: ChatIdType): List[NewsResult] = {
    val newsResults = newsList.flatMap { newsObject =>
      val newsResultEither = for {
        response <- Try(httpClient.sendGetRequest(newsObject.url)).toEither
        document <- Try(Jsoup.parse(response)).toEither
        newsTuple <- Try(newsObject.parser(document)).toEither
      } yield NewsResult(newsTuple._1, newsTuple._2, newsObject)

      newsResultEither match {
        case Right(newsResult) => Some(newsResult)
        case Left(e) =>
          println(s"[ERROR] ${e.getMessage}")
          None
      }
    }

    val newsInitMessage = s"Awali harimu dengan berita ${new String(Array(0x1f4f0),0,1)} dari **Seanmctoday** by @seanmcbot\n\n"
    val newsMessage = newsResults.foldLeft(newsInitMessage) { (message, res) =>
      message + s"${new String(res.newsObject.flag, 0, res.newsObject.flag.length)} ${res.newsObject.name} - [${res.title}](${res.url})\n\n"
    }
    
    telegramClient.sendMessage(chatIdType.i, newsMessage)
    newsResults
  }
}
