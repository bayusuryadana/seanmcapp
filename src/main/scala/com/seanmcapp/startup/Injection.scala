package com.seanmcapp.startup

import com.seanmcapp.api.{Service, TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository.postgre._

trait Injection {

  trait ServiceImpl extends Service {
    override val customerRepo = new CustomerRepoImpl
    override val photoRepo = new PhotoRepoImpl
    override val voteRepo = new VoteRepoImpl
    override val trackRepo = new TrackRepoImpl
    override val accountRepo = new AccountRepoImpl
  }

  val webAPI = new WebAPI with ServiceImpl

  val telegramAPI = new TelegramAPI with ServiceImpl

  val instagramFetcher = new InstagramFetcher with ServiceImpl

}
