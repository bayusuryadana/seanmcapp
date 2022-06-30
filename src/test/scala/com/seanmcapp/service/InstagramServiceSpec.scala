package com.seanmcapp.service

import com.seanmcapp.external._
import com.seanmcapp.repository.CacheRepoMock
import com.seanmcapp.repository.instagram.{Account, AccountGroupType, AccountGroupTypes, AccountRepo}
import com.seanmcapp.util.ChatIdTypes
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, times, verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class InstagramServiceSpec extends AnyWordSpec with Matchers {

  "process story" in {
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
    val accountRepo = mock(classOf[AccountRepo])
    val account = Account("123","a",AccountGroupTypes.Stalker)
    val accounts = Seq(account)
    when(accountRepo.getAll(any())).thenReturn(Future.successful(accounts))
    val stalkerService = new InstagramService(instagramClient, telegramClient, CacheRepoMock, accountRepo)
    stalkerService.processStory(ChatIdTypes.Personal, Set.empty[String], instagramStoryResponse, account)
    verify(telegramClient, times(1)).sendPhotoWithFileUpload(any(), any(), any())
    verify(telegramClient, times(1)).sendVideoWithFileUpload(any(), any(), any())
  }
  
  "process post" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val telegramResponse = Mockito.mock(classOf[TelegramResponse])
    when(telegramClient.sendPhotoWithFileUpload(any(), any(), any)).thenReturn(telegramResponse)
    when(telegramClient.sendVideoWithFileUpload(any(), any(), any)).thenReturn(telegramResponse)

    val instagramClient = mock(classOf[InstagramClient])
    when(instagramClient.postLogin()).thenReturn("")

    val instagramNodes = List(
      InstagramNode(
        "2115041543081728221",
        "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
        1566352621,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Hasna Izdihar. Magister Kenotariatan 2019\n#ugmcantik")))),
        "https://scontent-sin2-1.cdninstagram.com/vp/43571e6fb4e7d46752da94fc854a73f6/5E13F043/t51.2885-15/fr/e15/s1080x1080/67447213_218558379121900_7578318177720267803_n.jpg?_nc_ht=scontent-sin2-1.cdninstagram.com",
        false,
        None,
        None
      ),
      InstagramNode(
        "2114593284248831645",
        "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
        1566299184,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Just a caption")))),
        "https://scontent-sin2-1.cdninstagram.com/vp/b87609220db141e7db124b3c4f77185e/5DF5DF11/t51.2885-15/fr/e15/p1080x1080/66661723_494746641100189_7024627927498649957_n.jpg?_nc_ht=scontent-sin2-1.cdninstagram.com",
        false,
        None,
        None
      ),
      InstagramNode(
        "2114394029768926432",
        "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
        1566275431,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Yuan Shafira. FT 2019")))),
        "https://scontent-sin2-1.cdninstagram.com/vp/e81262628c99ba6a42596f2f81e36e2c/5E0A0E88/t51.2885-15/e15/66660256_2936655829742039_2990571628404942465_n.jpg?_nc_ht=scontent-sin2-1.cdninstagram.com",
        false,
        None,
        None
      ),
      InstagramNode(
        "123",
        "thumbnail_src",
        123,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Video")))),
        "display_url",
        true,
        Some("video_url"),
        None
      )
    )
    val accountRepo = mock(classOf[AccountRepo])
    val account = Account("123","a",AccountGroupTypes.Stalker)
    val accounts = Seq(account)
    when(accountRepo.getAll(any())).thenReturn(Future.successful(accounts))
    val stalkerService = new InstagramService(instagramClient, telegramClient, CacheRepoMock, accountRepo)
    stalkerService.processPost(ChatIdTypes.Personal, Set.empty[String], instagramNodes, account)
    verify(telegramClient, times(3)).sendPhotoWithFileUpload(any(), any(), any())
    verify(telegramClient, times(1)).sendVideoWithFileUpload(any(), any(), any())
  }

}
