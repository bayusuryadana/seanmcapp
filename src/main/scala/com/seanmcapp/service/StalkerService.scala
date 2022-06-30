package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.CacheRepo
import com.seanmcapp.repository.instagram.{AccountGroupType, AccountRepo}
import com.seanmcapp.util.ChatIdType

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
class StalkerService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo, accountRepo: AccountRepo) 
  extends InstagramService(instagramClient, telegramClient, cacheRepo, accountRepo) with ScheduledTask {

  override def run(): Future[Seq[TelegramResponse]] = {
    val postsF = fetchPosts(AccountGroupType.Stalker, ChatIdType.Group)
    val storiesF = fetchStories(AccountGroupType.Stalker, ChatIdType.Group)
    
    for {
      posts <- postsF
      stories <- storiesF
    } yield {
      posts ++ stories
    }
  }

}
