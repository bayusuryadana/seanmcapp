package com.seanmcapp.startup

import com.google.common.cache.CacheBuilder
import com.seanmcapp.service.{DotaService, Service, TelegramService, WebService}
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.{ArrayResponse, MatchResponse, PeerResponse}

import scalacache.{Cache, Entry}
import scalacache.guava.GuavaCache

trait Injection {

  trait ServiceImpl extends Service {
    override val customerRepo = CustomerRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val voteRepo = VoteRepoImpl
    override val trackRepo = TrackRepoImpl
  }

  val webAPI = new WebService with ServiceImpl

  val telegramAPI = new TelegramService with ServiceImpl

  val dotaAPI = new DotaService {
    override val playerRepo = PlayerRepoImpl
    override val heroRepo = HeroRepoImpl

    override val matchesCache = createCache[Seq[MatchResponse]]
    override val peersCache = createCache[Seq[PeerResponse]]

    private def createCache[T]: Cache[T] = {
      val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
      GuavaCache(underlyingGuavaCache)
    }
  }

}
