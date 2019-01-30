package com.seanmcapp.startup

import com.seanmcapp.api.{Service, TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository.mysql._

trait Injection {

  trait ServiceImpl extends Service {
    override val customerRepo = CustomerRepoImpl
    override val photoRepo = PhotoRepoImpl
    override val voteRepo = VoteRepoImpl
    override val trackRepo = TrackRepoImpl
    override val accountRepo = AccountRepoImpl
  }

  val webAPI = new WebAPI with ServiceImpl

  val telegramAPI = new TelegramAPI with ServiceImpl

  val instagramFetcher = new InstagramFetcher with ServiceImpl

}
