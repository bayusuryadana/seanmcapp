package com.seanmcapp.service

import com.seanmcapp.external.{TelegramClient, TweetObject, TwitterClient}
import com.seanmcapp.repository.{Cache, CacheRepo}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TwitterService(twitterClient: TwitterClient, cacheRepo: CacheRepo, telegramClient: TelegramClient) extends ScheduledTask {

  private val accountMap = Map(
    "Alvida" -> "67603103",
    "Buggy" -> "159846549"
  )

  // val chatId = -1001359004262L
  val chatId = 274852283L

  private val tweetPrefix = "tweet-"
  private val likedPrefix = "liked-"

  override def run: Future[Map[String, List[TweetObject]]] = {
    val cacheF = cacheRepo.getAll()

    cacheF.map { cache =>
      accountMap.map { case (name, id) =>
        val tweetResponse = processData(cache, name, id, tweetPrefix)
        val likedResponse = processData(cache, name, id, likedPrefix)

        name -> (tweetResponse ++ likedResponse)
      }
    }
  }

  private def processData(cache: Seq[Cache], name: String, id: String, prefix: String): List[TweetObject] = {
    val filteredCache = cache.filter(_.key.contains(s"$prefix$id")).flatMap(_.value.split(",")).toSet
    val (action, response) = prefix match {
      case s if s == tweetPrefix =>
        ("Tweet", twitterClient.getTweets(id).data)
      case s if s == likedPrefix =>
        ("Liked tweet", twitterClient.getLiked(id).data)
      case _ => ("", List.empty[TweetObject])
    }
    val nonViewedT = response.filterNot(tw => filteredCache.contains(tw.id))

    // sending tweet
    nonViewedT.foreach { tweetObj =>
      val isReply = if (tweetObj.referenced_tweets.isDefined) "reply" else ""
      val escapedText = tweetObj.text.replace("_", "\\_")
      val text = s"$name - $action\n$escapedText $isReply"
      telegramClient.sendMessage(chatId, text)
    }

    // delete and add cache
    cacheRepo.delete(s"$prefix$id")
    Thread.sleep(1000)
    cacheRepo.set(Cache(s"$prefix$id", response.map(_.id).foldLeft("")((res, s) => s"$res,$s"), None))

    response
  }
}
