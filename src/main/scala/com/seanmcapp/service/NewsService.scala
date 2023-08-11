package com.seanmcapp.service

import com.seanmcapp.external.{NewsClient, TelegramClient}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters._
import scala.util.Try

case class NewsResult(title: String, url: String, flag: Array[Int])

class NewsService(newsClient: NewsClient, telegramClient: TelegramClient) extends ScheduledTask {
  
  override def run: List[NewsResult] = {
    // news
    val newsResponses = newsClient.getNews
    val newsResults = newsResponses.toList.flatMap { case (key, response) =>
      val newsObject = NewsConstant.mapping(key)
      Try(newsObject.parser(Jsoup.parse(response))).toOption.map(res => (newsObject.order, res))
    }.sortBy(_._1).map(_._2)

    val newsInitMessage = s"Awali harimu dengan berita ${new String(Array(0x1f4f0),0,1)} dari **Seanmctoday** by @seanmcbot\n\n"
    val newsMessage = newsResults.zipWithIndex.foldLeft(newsInitMessage) { (message, res) =>
      message + s"${res._2+1}. [${res._1.title}](${res._1.url}) ${new String(res._1.flag, 0, res._1.flag.length)}\n\n"
    }
    
    telegramClient.sendMessage(-1001359004262L, newsMessage)
    newsResults
  }

}

case class NewsObject(order: Int, url: String, parser: Document => NewsResult)

object NewsConstant {
  val mapping = Map(
    "tirto" -> NewsObject(1, "https://tirto.id", tirtoParser),
//    "kumparan" -> NewsObject(2, "https://kumparan.com/trending", kumparanParser),
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
    val tag = d.selectFirst(".Viewweb__StyledView-sc-1ajfkkc-0.fswdpV .LabelLinkweb__StyledLink-sc-fupmuj-0.btFwc")
    println(tag.text())
    NewsResult(
      tag.selectFirst("span").text(),
      s"https://kumparan.com${tag.attr("href")}",
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
    val tag = d.select(".welcome-title").asScala.toList(6).parent.parent.parent.selectFirst(".mb-3 a")
    NewsResult(tag.text(), s"https://tirto.id${tag.attr("href")}", Array(0x1f1ee, 0x1f1e9))
  }

}
