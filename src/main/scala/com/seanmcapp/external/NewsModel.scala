package com.seanmcapp.external

import com.seanmcapp.external.NewsObject.{NewsTitle, NewsUrl}
import io.circe.{Json, Encoder => CirceEncoder}
import org.jsoup.nodes.Document

import scala.jdk.CollectionConverters._

case class NewsResult(title: String, url: String, newsObject: NewsObject)

object NewsResult {
  implicit val newsResultEncoder: CirceEncoder[NewsResult] = (nr: NewsResult) => Json.obj(
    ("title", Json.fromString(nr.title)), ("url", Json.fromString(nr.url))
  )
}

object NewsObject {
  type NewsTitle = String
  type NewsUrl = String
}

trait NewsObject {
  val name: String
  val url: String
  val flag: Array[Int]
  def parser(d: Document): (NewsTitle, NewsUrl)
}

object Detik extends NewsObject {
  val name = "Detik"
  val url = "https://www.detik.com/"
  val flag = Array(0x1f1ee, 0x1f1e9)
  def parser(d: Document):  (NewsTitle, NewsUrl) = {
    val tag = d.selectFirst("[dtr-evt=headline]")
    (tag.attr("dtr-ttl"), tag.attr("href"))
  }
}

object Tirtol extends NewsObject {
  val name = "Tirtol"
  val url = "https://tirto.id"
  val flag = Array(0x1f1ee, 0x1f1e9)
  def parser(d: Document):  (NewsTitle, NewsUrl) = {
    val popularTitleOpt = d.select(".welcome-title").asScala.toList.find(_.text() == "POPULER")
    popularTitleOpt match {
      case Some(popularTitle) =>
        val tag = popularTitle.parent.parent.parent.selectFirst(".mb-3 a")
        (tag.text(), s"https://tirto.id${tag.attr("href")}")
      case _ =>
        val errorMessage = s"[ERROR] POPULAR section not found from Tirtol"
        println(errorMessage)
        throw new Exception(errorMessage)
    }
  }
}

object Kumparan extends NewsObject {
  val name = "Kumparan"
  val url = "https://kumparan.com/trending"
  val flag = Array(0x1f1ee, 0x1f1e9)
  def parser(d: Document):  (NewsTitle, NewsUrl) = {
    val tag = d.selectFirst("[data-qa-id=news-item]")
    (
      tag.selectFirst("[data-qa-id=title]").text(),
      s"https://kumparan.com${tag.selectFirst("a").attr("href")}"
    )
  }
}

object Mothership extends NewsObject {
  val name = "Mothership"
  val url = "https://mothership.sg"
  val flag = Array(0x1f1f8, 0x1f1ec)
  def parser(d: Document):  (NewsTitle, NewsUrl) = {
    val tag = d.selectFirst(".main-item > .top-story")
    (tag.selectFirst("h1").text(), tag.selectFirst("a").attr("href"))
  }
}

object CNA extends NewsObject {
  val name = "CNA"
  val url = "https://www.channelnewsasia.com/news/singapore"
  val flag = Array(0x1f1f8, 0x1f1ec)
  def parser(d: Document):  (NewsTitle, NewsUrl) = {
    val tag = d.selectFirst(".card-object h3")
    (tag.text(), s"https://www.channelnewsasia.com${tag.selectFirst("a").attr("href")}")
  }
}

object Reuters extends NewsObject {
  val name = "Reuters"
  val url = "https://www.reuters.com"
  val flag = Array(0x1f30f)
  def parser(d: Document):  (NewsTitle, NewsUrl) = {
    val tag = d.selectFirst("#main-content").selectFirst("[href=/world/]").parent.parent.selectFirst("[data-testid=Heading]")
    (tag.text(), s"https://www.reuters.com${tag.attr("href")}")
  }
}
