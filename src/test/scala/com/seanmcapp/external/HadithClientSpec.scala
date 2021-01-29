package com.seanmcapp.external

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HadithClientSpec extends AnyWordSpec with Matchers {

  "random" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val hadithClient = new HadithClient(http)
    val response =
      s"""
         |{
         |    "collection": "riyadussalihin",
         |    "bookNumber": "8",
         |    "chapterId": "212.00",
         |    "hadithNumber": "1181",
         |    "hadith": [
         |        {
         |            "lang": "en",
         |            "chapterNumber": "212",
         |            "chapterTitle": "Excellence of Standing in Prayer at Night",
         |            "urn": 1622250,
         |            "body": "<p>'Aishah (May Allah be pleased with her) reported: If the Messenger of Allah (ﷺ) missed his night (Tahajjud) Salat because of indisposition or the like, he would perform twelve Rak'ah during the day.<br/><br/><b>[Muslim]</b>.</p>",
         |            "grades": []
         |        },
         |        {
         |            "lang": "ar",
         |            "chapterNumber": "212",
         |            "chapterTitle": "- باب فضل قيام الليل‏:‏",
         |            "urn": 1711710,
         |            "body": "<p>- وعنها رضي الله عنها، قالت‏:‏ كان رسول الله صلى الله عليه وسلم ، إذا فاتته الصلاة من الليل من وجع أو غيره، صلى من النهار ثنتي عشر ركعة‏.‏ ‏(‏‏(‏رواه مسلم‏)‏‏)‏‏.‏</p>",
         |            "grades": []
         |        }
         |    ]
         |}
         |""".stripMargin
    when(http.sendGetRequest(any(), any())).thenReturn(response)
    val expected = HadithDataResponse("riyadussalihin","8","212.00","1181",
      List(
        Hadith("en","212","Excellence of Standing in Prayer at Night",1622250,"'Aishah (May Allah be pleased with her) reported: If the Messenger of Allah (ﷺ) missed his night (Tahajjud) Salat because of indisposition or the like, he would perform twelve Rak'ah during the day. [Muslim].", List()),
        Hadith("ar","212","- باب فضل قيام الليل‏:‏", 1711710, "- وعنها رضي الله عنها، قالت‏:‏ كان رسول الله صلى الله عليه وسلم ، إذا فاتته الصلاة من الليل من وجع أو غيره، صلى من النهار ثنتي عشر ركعة‏.‏ ‏(‏‏(‏رواه مسلم‏)‏‏)‏‏.‏", List())
    ))
    hadithClient.random shouldBe expected
  }

}
