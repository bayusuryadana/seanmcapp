package com.seanmcapp.service

import com.seanmcapp.external.{TelegramClient, TweetObject, TweetReferencedObject, TweetResponse, TwitterClient}
import com.seanmcapp.repository.CacheRepoMock
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TwitterServiceSpec extends AnyWordSpec with Matchers {

  "run" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val twitterClient = Mockito.mock(classOf[TwitterClient])

    val mockTweet = TweetResponse(List(
      TweetObject("tweet 1", "101", "2021-11-24T18:36:17.000Z", Some(List(TweetReferencedObject("201", "retweeted")))),
      TweetObject("tweet 2", "102", "2021-11-24T08:03:29.000Z", None),
    ))
    when(twitterClient.getTweets(any())).thenReturn(mockTweet)

    val mockLiked = TweetResponse(List(
      TweetObject("liked 1", "101", "2021-11-07T18:02:45.000Z", None),
      TweetObject("liked 2", "102", "2021-11-09T09:00:19.000Z", None),
    ))
    when(twitterClient.getLiked(any())).thenReturn(mockLiked)

    val twitterService = new TwitterService(twitterClient, CacheRepoMock, telegramClient) {
      override private[service] val accountMap = Map("a" -> "1")
    }

    Await.result(twitterService.run(), Duration.Inf)
    verify(twitterClient, times(1)).getTweets(any())
    verify(twitterClient, times(1)).getLiked(any())
    verify(telegramClient, times(4)).sendMessage(any(), any())
  }

}
