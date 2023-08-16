package com.seanmcapp

import com.seanmcapp.external._
import com.seanmcapp.repository.{CacheRepo, CacheRepoImpl, FileRepo, FileRepoImpl}
import com.seanmcapp.repository.birthday.{PeopleRepo, PeopleRepoImpl}
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.repository.seanmcmamen.{StallRepo, StallRepoImpl}
import com.seanmcapp.repository.seanmcwallet.{WalletRepo, WalletRepoImpl}
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
  val cacheRepo: CacheRepo = CacheRepoImpl
  val accountRepo: AccountRepo = AccountRepoImpl
  val stallRepo: StallRepo = StallRepoImpl

  val httpClient: HttpRequestClient = HttpRequestClientImpl
  val telegramClient = new TelegramClient(httpClient)
  val telegramClient2 = new TelegramClient2(httpClient)

  val birthdayService = new BirthdayService(peopleRepo, telegramClient)
  
  val cacheCleanerService = new CacheCleanerService(cacheRepo)

  val cbcClient = new CBCClient(httpClient)
  val cbcService = new CBCService(photoRepo, customerRepo, fileRepo, accountRepo, cbcClient, instagramClient)
  
  val googleClient = new GoogleClient(httpClient)
  val mamenService = new MamenService(stallRepo, googleClient)

  val dotaClient = new DotaClient(httpClient)
  val dotaService = new DotaService(playerRepo, heroRepo, heroAttributeRepo, dotaClient)

  val hadithClient = new HadithClient(httpClient)
  val hadithService = new HadithService(hadithClient)
  
  val instagramClient = new InstagramClient(httpClient)

  val newsService = new NewsService(httpClient, telegramClient)
  
  val stalkerService = new StalkerService(instagramClient, telegramClient, cacheRepo, accountRepo)
  val specialStalkerService = new StalkerSpecialService(instagramClient, telegramClient2, cacheRepo, accountRepo)

  val walletService = new WalletService(walletRepo)

  val warmupDBService = new WarmupDBService(peopleRepo)

  val telegramWebhookService = new TelegramWebhookService(cbcService, hadithService, telegramClient)
  
  val twitterClient = new TwitterClient(httpClient)
  val twitterService = new TwitterService(twitterClient, cacheRepo, telegramClient)
}
