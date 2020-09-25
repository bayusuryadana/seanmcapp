package com.seanmcapp

import com.seanmcapp.external._
import com.seanmcapp.repository.{FileRepo, FileRepoImpl}
import com.seanmcapp.repository.birthday.{PeopleRepo, PeopleRepoImpl}
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
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

  val httpClient: HttpRequestClient = HttpRequestClientImpl
  val telegramClient = new TelegramClient(httpClient)

  val airVisualClient = new AirVisualClient(httpClient)
  val airVisualService = new AirVisualService(airVisualClient, telegramClient)

  val amarthaClient = new AmarthaClient(httpClient)
  val amarthaService = new AmarthaService(amarthaClient, telegramClient)

  val birthdayService = new BirthdayService(peopleRepo, telegramClient)

  val broadcastService = new BroadcastService(telegramClient)

  val cbcClient = new CBCClient(httpClient)
  val cbcService = new CBCService(photoRepo, customerRepo, cbcClient, telegramClient)

  val dotaClient = new DotaClient(httpClient)
  val dotaService = new DotaService(playerRepo, heroRepo, heroAttributeRepo, dotaClient)

  val dsdaJakartaClient = new DsdaJakartaClient(httpClient)
  val dsdaJakartaService = new DsdaJakartaService(dsdaJakartaClient, telegramClient)

  val iGrowClient = new IGrowClient(httpClient)
  val iGrowService = new IGrowService(iGrowClient, telegramClient)

  val instagramClient = new InstagramClient(httpClient)
  val instagramService = new InstagramService(photoRepo, fileRepo, instagramClient)

  val nCovClient = new NCovClient(httpClient)
  val nCovService = new NCovService(nCovClient, telegramClient)

  val walletService = new WalletService(walletRepo)

  val stockClient = new StockClient(httpClient)
  val stockService = new StockService(stockClient)

  val warmupDBService = new WarmupDBService(peopleRepo)

}
