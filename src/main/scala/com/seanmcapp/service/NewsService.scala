package com.seanmcapp.service

import java.net.URLEncoder

import com.seanmcapp.external.{NewsClient, TelegramClient}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters._

case class NewsResult(title: String, url: String, flag: Array[Int])

class NewsService(newsClient: NewsClient, telegramClient: TelegramClient) extends ScheduledTask {

  override def run: List[NewsResult] = {
    val responses = newsClient.getNews
    val results = responses.toList.map { case (key, response) =>
      val newsObject = NewsConstant.mapping(key)
      val result = newsObject.parser(Jsoup.parse(response))
      (newsObject.order, result)
    }.sortBy(_._1).map(_._2)

    val initMessage = s"Awali harimu dengan berita ${new String(Array(0x1f4f0),0,1)} dari **Seanmctoday** by @seanmcbot\n\n"
    val message = results.zipWithIndex.foldLeft(initMessage) { (message, res) =>
      message + s"${res._2+1}. [${res._1.title}](${res._1.url}) ${new String(res._1.flag, 0, res._1.flag.length)}\n\n"
    }
    telegramClient.sendMessage(-1001359004262L, URLEncoder.encode(message, "UTF-8"))

    results
  }

}

case class NewsObject(order: Int, url: String, parser: Document => NewsResult)

object NewsConstant {
  val mapping = Map(
    "tirto" -> NewsObject(1, "https://tirto.id", tirtoParser),
    "kumparan" -> NewsObject(2, "https://kumparan.com/trending", kumparanParser),
    "mothership" -> NewsObject(3, "https://mothership.sg", mothershipParser),
    "cna" -> NewsObject(4, "https://www.channelnewsasia.com/news/singapore", cnaParser),
    "reuters" -> NewsObject(5, "https://www.reuters.com", reutersParser),
  )

  private def cnaParser(d: Document): NewsResult = {
    val tag = d.selectFirst(".hero-article h3")
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
    val tag = d.selectFirst("#topStory h2 a")
    NewsResult(tag.text(), s"https://www.reuters.com${tag.attr("href")}", Array(0x1f30f))
  }

  private def tirtoParser(d: Document): NewsResult = {
    // TODO: check whether the title matches 'POPULER', instead of hard code the index 8
    val tag = d.select(".welcome-title").asScala.toList(8).parent.parent.parent.selectFirst(".mb-3 a")
    NewsResult(tag.text(), s"https://tirto.id${tag.attr("href")}", Array(0x1f1ee, 0x1f1e9))
  }

}
