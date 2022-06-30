package com.seanmcapp

import com.seanmcapp.external._
import com.seanmcapp.repository.{CacheRepo, CacheRepoImpl, FileRepo, FileRepoImpl}
import com.seanmcapp.repository.birthday.{PeopleRepo, PeopleRepoImpl}
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.repository.seanmcwallet.{WalletRepo, WalletRepoDemo, WalletRepoImpl}
import com.seanmcapp.service._

// $COVERAGE-OFF$
trait Injection {

  val peopleRepo: PeopleRepo = PeopleRepoImpl
  val photoRepo: PhotoRepo = PhotoRepoImpl
  val customerRepo: CustomerRepo = CustomerRepoImpl
  val playerRepo: PlayerRepo = PlayerRepoImpl
  val heroRepo: HeroRepo = HeroRepoImpl
  val heroAttributeRepo: HeroAttributeRepo = HeroAttributeRepoImpl
  val fileRepo: FileRepo = FileRepoImpl
  val walletRepo: WalletRepo = WalletRepoImpl
  val walletRepoDemo: WalletRepo = WalletRepoDemo
  val cacheRepo: CacheRepo = CacheRepoImpl
  val accountRepo: AccountRepo = AccountRepoImpl

  val httpClient: HttpRequestClient = HttpRequestClientImpl
  val telegramClient = new TelegramClient(httpClient)
  val telegramClient2 = new TelegramClient2(httpClient)

  val airVisualClient = new AirVisualClient(httpClient)
  val airVisualService = new AirVisualService(airVisualClient, telegramClient)

  val birthdayService = new BirthdayService(peopleRepo, telegramClient)

  val broadcastService = new BroadcastService(telegramClient)

  val cbcClient = new CBCClient(httpClient)
  val cbcService = new CBCService(photoRepo, customerRepo, fileRepo, accountRepo, cbcClient, instagramClient)

  val dotaClient = new DotaClient(httpClient)
  val dotaService = new DotaService(playerRepo, heroRepo, heroAttributeRepo, dotaClient)

  val dsdaJakartaClient = new DsdaJakartaClient(httpClient)
  val dsdaJakartaService = new DsdaJakartaService(dsdaJakartaClient, telegramClient)

  val hadithClient = new HadithClient(httpClient)
  val hadithService = new HadithService(hadithClient)
  
  val instagramClient = new InstagramClient(httpClient)
  //val instagramService = new InstagramService(instagramClient, telegramClient, cacheRepo, accountRepo)

  val nCovClient = new NCovClient(httpClient)
  val nCovService = new NCovService(nCovClient, telegramClient)

  val newsClient = new NewsClient(httpClient)
  val newsService = new NewsService(newsClient, telegramClient)
  
  val stalkerService = new StalkerService(instagramClient, telegramClient, cacheRepo, accountRepo)
  val specialStalkerService = new StalkerSpecialService(instagramClient, telegramClient2, cacheRepo, accountRepo)

  val walletService = new WalletService(walletRepo, walletRepoDemo)

  val warmupDBService = new WarmupDBService(peopleRepo)

  val telegramWebhookService = new TelegramWebhookService(cbcService, hadithService, telegramClient)

  val cacheCleanerService = new CacheCleanerService(cacheRepo)

  val twitterClient = new TwitterClient(httpClient)
  val twitterService = new TwitterService(twitterClient, cacheRepo, telegramClient)
}
