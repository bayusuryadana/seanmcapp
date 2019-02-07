package com.seanmcapp.startup

import com.seanmcapp.service.{Service, TelegramService, WebService}
import com.seanmcapp.fetcher.DotaFetcher
import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._

trait Injection {

  val dotaFetcher = new DotaFetcher {
    override val playerRepo = PlayerRepoImpl
    override val matchRepo = MatchRepoImpl
    override val peerRepo = PeerRepoImpl
  }

  trait ServiceImpl extends Service {
    override val customerRepo = CustomerRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val voteRepo = VoteRepoImpl
    override val trackRepo = TrackRepoImpl
  }

  val webAPI = new WebService with ServiceImpl

  val telegramAPI = new TelegramService with ServiceImpl

}
