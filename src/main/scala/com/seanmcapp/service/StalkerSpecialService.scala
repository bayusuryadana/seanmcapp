package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.CacheRepo
import com.seanmcapp.repository.instagram.{AccountGroupType, AccountRepo}
import com.seanmcapp.util.ChatIdType

import scala.concurrent.Future

// $COVERAGE-OFF$
class StalkerSpecialService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo, accountRepo: AccountRepo)
  extends InstagramService(instagramClient, telegramClient, cacheRepo, accountRepo) with ScheduledTask {

  override def run(): Future[Seq[TelegramResponse]] = fetchPosts(AccountGroupType.StalkerSpecial, ChatIdType.Personal)
  
}
