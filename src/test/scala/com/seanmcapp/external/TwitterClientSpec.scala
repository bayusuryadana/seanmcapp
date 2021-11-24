package com.seanmcapp.external

import org.mockito.Mockito
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class TwitterClientSpec extends AnyWordSpec with Matchers {

  val http = Mockito.mock(classOf[HttpRequestClient])
  val twitterClient = new TwitterClient(http)
  val mockId = "1"

  "getTweet" in {
    val response = Source.fromResource("twitter/tweet.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(response)
    val expected = TweetResponse(List(
      TweetObject("tweet 1", "101", "2021-11-24T18:36:17.000Z", Some(List(TweetReferencedObject("201", "retweeted")))),
      TweetObject("tweet 2", "102", "2021-11-24T08:03:29.000Z", None),
    ))
    twitterClient.getTweets(mockId) shouldBe expected
  }

  "getLiked" in {
    val response = Source.fromResource("twitter/liked.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(response)
    val expected = TweetResponse(List(
      TweetObject("liked 1", "101", "2021-11-07T18:02:45.000Z", None),
      TweetObject("liked 2", "102", "2021-11-09T09:00:19.000Z", None),
    ))
    twitterClient.getTweets(mockId) shouldBe expected
  }

}
