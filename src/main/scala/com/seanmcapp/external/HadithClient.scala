package com.seanmcapp.external

import com.seanmcapp.HadithConf
import org.jsoup.Jsoup

case class HadithGrade(grade: String)
case class Hadith(lang: String, chapterNumber: String, chapterTitle: String, urn: Int, body: String, grades: List[HadithGrade])
case class HadithDataResponse(collection: String, bookNumber: String, chapterId: String, hadithNumber: String,
                              hadith: List[Hadith])

class HadithClient(http: HttpRequestClient) {

  private val config = HadithConf()
  private val url = config.endpoint.getOrElse("https://api.sunnah.com/v1/hadiths/random")
  private val headers = HeaderMap(Map("x-api-key" -> config.key))

  def random: HadithDataResponse = {
    val response = decode[HadithDataResponse](http.sendGetRequest(url, headers = Some(headers)))
    val cleanedHadith = response.hadith.map { h =>
      h.copy(body = Jsoup.parse(StringContext.processEscapes(h.body)).text())
    }
    response.copy(hadith = cleanedHadith)
  }

}
