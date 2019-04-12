package com.seanmcapp.startup

import com.google.common.cache.CacheBuilder
import com.seanmcapp.repository.birthday.{PeopleRepo, PeopleRepoImpl}
import com.seanmcapp.service.{DotaService, CBCService}
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import com.seanmcapp.view.{BirthdayView, DotaView, TelegramView, WebView}
import scalacache.{Cache, Entry}
import scalacache.guava.GuavaCache

trait Injection {

  trait CBCServiceImpl extends CBCService {
    override val customerRepo = CustomerRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val voteRepo = VoteRepoImpl
    override val trackRepo = TrackRepoImpl
  }

  val webAPI = new WebView with CBCServiceImpl

  val telegramAPI = new TelegramView with CBCServiceImpl

  trait DotaServiceImpl extends DotaService {
    override val playerRepo = PlayerRepoImpl
    override val heroRepo = HeroRepoImpl

    override val matchesCache = createCache[Seq[MatchResponse]]
    override val peersCache = createCache[Seq[PeerResponse]]

    private def createCache[T]: Cache[T] = {
      val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
      GuavaCache(underlyingGuavaCache)
    }
  }

  val dotaAPP = new DotaView with DotaServiceImpl

  val birthdayAPI = new BirthdayView {
    override val peopleRepo: PeopleRepo = PeopleRepoImpl
  }

}
