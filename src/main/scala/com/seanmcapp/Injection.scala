package com.seanmcapp

import com.seanmcapp.external._
import com.seanmcapp.repository.birthday.{PeopleRepo, PeopleRepoImpl}
import com.seanmcapp.repository.{WalletRepo, WalletRepoImpl}
import com.seanmcapp.service._

// $COVERAGE-OFF$
trait Injection {

  val peopleRepo: PeopleRepo = PeopleRepoImpl
  val walletRepo: WalletRepo = WalletRepoImpl

  val httpClient: HttpRequestClient = HttpRequestClientImpl
  val telegramClient = new TelegramClient(httpClient)

  val birthdayService = new BirthdayService(peopleRepo, telegramClient)

  val newsService = new NewsService(httpClient, telegramClient)

  val warmupDBService = new WarmupDBService(peopleRepo)

  val telegramWebhookService = new TelegramWebhookService(telegramClient)
}
