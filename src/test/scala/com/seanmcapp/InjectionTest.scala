package com.seanmcapp

import com.google.common.cache.CacheBuilder
import com.seanmcapp.mock.repository.{HeroRepoMock, PlayerRepoMock}
import com.seanmcapp.repository.dota.{HeroRepo, PlayerRepo}
import com.seanmcapp.repository.instagram._
import com.seanmcapp.service._
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import scalacache.guava.GuavaCache
import scalacache.{Cache, Entry}

trait InjectionTest {

  private val customerRepoImpl = CustomerRepoImpl
  private val photoRepoImpl = PhotoRepoImpl
  private val voteRepoImpl = VoteRepoImpl
  private val trackRepoImpl = TrackRepoImpl

  trait CBCServiceImpl extends CBCService {
    override val customerRepo = customerRepoImpl
    override val photoRepo = photoRepoImpl
    override val voteRepo = voteRepoImpl
    override val trackRepo = trackRepoImpl
  }

  val webService = new WebService with CBCServiceImpl

  val telegramService = new TelegramService with CBCServiceImpl

  val dotaService = new DotaService {
    override val playerRepo: PlayerRepo = PlayerRepoMock
    override val heroRepo: HeroRepo = HeroRepoMock

    override val matchesCache = createCache[Seq[MatchResponse]]
    override val peersCache = createCache[Seq[PeerResponse]]

    private def createCache[T]: Cache[T] = {
      val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
      GuavaCache(underlyingGuavaCache)
    }
  }

}
