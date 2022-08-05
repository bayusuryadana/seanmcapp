package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.CacheRepo
import com.seanmcapp.repository.instagram.{AccountGroupTypes, AccountRepo}
import com.seanmcapp.util.ChatIdTypes

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
class StalkerService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo, accountRepo: AccountRepo) 
  extends InstagramService(instagramClient, telegramClient, cacheRepo, accountRepo) with ScheduledTask {

  override def run(): Future[Seq[TelegramResponse]] = {
    val postsF = fetchPosts(AccountGroupTypes.Stalker, ChatIdTypes.Group)
    val storiesF = fetchStories(AccountGroupTypes.Stalker, ChatIdTypes.Group)
    
    for {
      posts <- postsF
      stories <- storiesF
    } yield {
      posts ++ stories
    }
  }

}
