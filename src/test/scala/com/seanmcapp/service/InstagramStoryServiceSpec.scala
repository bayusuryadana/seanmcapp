package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.CacheRepoMock
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class InstagramStoryServiceSpec extends AnyWordSpec with Matchers {

  "Scheduler" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val telegramResponse = Mockito.mock(classOf[TelegramResponse])
    when(telegramClient.sendPhotoWithFileUpload(any(), any(), any)).thenReturn(telegramResponse)
    when(telegramClient.sendVideoWithFileUpload(any(), any(), any)).thenReturn(telegramResponse)

    val instagramClient = mock(classOf[InstagramClient])
    when(instagramClient.postLogin()).thenReturn("")
    val instagramStoryResponse = InstagramStoryResponse(
      InstagramStoryData(List(
        InstagramStoryReel(List(
          InstagramStoryItem("2424716613985265804","GraphStoryImage","https://pic1.url",None),
          InstagramStoryItem("2424802368570123380","GraphStoryVideo","https://pic2.url",
            Some(List(
              InstagramStoryVideoResource("https://video-baseline.url","BASELINE"),
              InstagramStoryVideoResource("https://video-main.url","MAIN"))
            ))
        ))
      ))
    )
    when(instagramClient.getStories(any(), any())).thenReturn(instagramStoryResponse)
    val instagramStoryService = new InstagramStoryService(instagramClient, telegramClient, CacheRepoMock) {
      override private[service] def getDataByte(url: String) = Array.emptyByteArray
    }
    val result = instagramStoryService.run()
    result shouldBe List(
      "https://pic1.url", "https://video-main.url",
      "https://pic1.url", "https://video-main.url",
      "https://pic1.url", "https://video-main.url"
    )
  }

}
