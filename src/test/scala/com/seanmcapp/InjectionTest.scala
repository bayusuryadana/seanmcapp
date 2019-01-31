package com.seanmcapp

import com.seanmcapp.api.{Service, TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository.mysql._

trait InjectionTest {

  private val customerRepoImpl = new CustomerRepoImpl
  private val photoRepoImpl = new PhotoRepoImpl
  private val voteRepoImpl = new VoteRepoImpl
  private val trackRepoImpl = new TrackRepoImpl
  private val accountRepoImpl = new AccountRepoImpl

  trait ServiceImpl extends Service {
    override val customerRepo = customerRepoImpl
    override val photoRepo = photoRepoImpl
    override val voteRepo = voteRepoImpl
    override val trackRepo = trackRepoImpl
    override val accountRepo = accountRepoImpl
  }

  val webAPI = new WebAPI with ServiceImpl

  val telegramAPI = new TelegramAPI with ServiceImpl

  val instagramFetcher = new InstagramFetcher with ServiceImpl

}
