package com.seanmcapp.service

import com.seanmcapp.external.{Hadith, HadithClient, HadithDataResponse}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HadithServiceSpec extends AnyWordSpec with Matchers {

  "get random" in {
    val hadithClientMock = Mockito.mock(classOf[HadithClient])
    val hadithResponse = HadithDataResponse("riyadussalihin","8","212.00","1181",
      List(
        Hadith("en","212","Excellence of Standing in Prayer at Night",1622250,"'Aishah (May Allah be pleased with her) reported: If the Messenger of Allah (ﷺ) missed his night (Tahajjud) Salat because of indisposition or the like, he would perform twelve Rak'ah during the day. [Muslim].", List()),
        Hadith("ar","212","- باب فضل قيام الليل‏:‏", 1711710, "- وعنها رضي الله عنها، قالت‏:‏ كان رسول الله صلى الله عليه وسلم ، إذا فاتته الصلاة من الليل من وجع أو غيره، صلى من النهار ثنتي عشر ركعة‏.‏ ‏(‏‏(‏رواه مسلم‏)‏‏)‏‏.‏", List())
      ))
    when(hadithClientMock.random).thenReturn(hadithResponse)
    val hadithService = new HadithService(hadithClientMock)

    hadithService.random.contains("riyadussalihin") shouldBe true
    hadithService.random.contains("أو غير") shouldBe true
    hadithService.random.contains("Tahajjud") shouldBe true
  }

}
