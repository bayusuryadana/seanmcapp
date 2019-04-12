package com.seanmcapp.startup

import com.google.common.cache.CacheBuilder
import com.seanmcapp.repository.birthday.{PeopleRepo, PeopleRepoImpl}
import com.seanmcapp.service.{BirthdayService, CBCService, DotaService, TelegramServiceBuilder, WebService}
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import scalacache.{Cache, Entry}
import scalacache.guava.GuavaCache

trait Injection {

  trait CBCServiceImpl extends CBCService {
    override val customerRepo = CustomerRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val voteRepo = VoteRepoImpl
    override val trackRepo = TrackRepoImpl
  }

  val webAPI = new WebService with CBCServiceImpl

  val telegramAPI = new TelegramServiceBuilder with CBCServiceImpl

  val dotaAPP = new DotaService {
    override val playerRepo = PlayerRepoImpl
    override val heroRepo = HeroRepoImpl

    override val matchesCache = createCache[Seq[MatchResponse]]
    override val peersCache = createCache[Seq[PeerResponse]]

    private def createCache[T]: Cache[T] = {
      val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
      GuavaCache(underlyingGuavaCache)
    }
  }

  val birthdayAPI = new BirthdayService {
    override val peopleRepo: PeopleRepo = PeopleRepoImpl
  }

}
