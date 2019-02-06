package com.seanmcapp.startup

import com.seanmcapp.api.{Service, TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.{DotaFetcher, Fetcher, InstagramFetcher}
import com.seanmcapp.repository.dota.{MatchRepoImpl, PeerRepoImpl, PlayerRepoImpl}
import com.seanmcapp.repository.instagram._

trait Injection {

  trait ServiceImpl extends Service {
    override val customerRepo = CustomerRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val voteRepo = VoteRepoImpl
    override val trackRepo = TrackRepoImpl
  }

  trait FetcherImpl extends Fetcher {
    override val accountRepo = AccountRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val playerRepo = PlayerRepoImpl
    override val matchRepo = MatchRepoImpl
    override val peerRepo = PeerRepoImpl
  }

  val webAPI = new WebAPI with ServiceImpl

  val telegramAPI = new TelegramAPI with ServiceImpl

  val instagramFetcher = new InstagramFetcher with FetcherImpl

  val dotaFetcher = new DotaFetcher with FetcherImpl

}
