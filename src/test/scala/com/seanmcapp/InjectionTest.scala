package com.seanmcapp

import com.google.common.cache.CacheBuilder
import com.seanmcapp.mock.repository._
import com.seanmcapp.mock.requestbuilder.{DotaRequestBuilderMock, TelegramRequestBuilderMock}
import com.seanmcapp.repository.dota.{HeroRepo, PlayerRepo}
import com.seanmcapp.service._
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import scalacache.guava.GuavaCache
import scalacache.{Cache, Entry}

trait CBCServiceImpl extends CBCService with TelegramRequestBuilderMock {
  override val photoRepo = PhotoRepoMock
}

trait DotaServiceImpl extends DotaService with DotaRequestBuilderMock {
  override val playerRepo: PlayerRepo = PlayerRepoMock
  override val heroRepo: HeroRepo = HeroRepoMock

  override val matchesCache = createCache[Seq[MatchResponse]]
  override val peersCache = createCache[Seq[PeerResponse]]

  private def createCache[T]: Cache[T] = {
    val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
    GuavaCache(underlyingGuavaCache)
  }
}
