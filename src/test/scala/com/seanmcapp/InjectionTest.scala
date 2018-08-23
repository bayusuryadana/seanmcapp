package com.seanmcapp

import com.seanmcapp.api.{TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository._
import com.seanmcapp.repository.mongodb.{AccountRepoImpl, CustomerRepoImpl, PhotoRepoImpl, VoteRepoImpl}

trait InjectionTest {

  // TODO: Mock services
  protected val customerRepoImpl = new CustomerRepoImpl
  protected val photoRepoImpl = new PhotoRepoImpl
  protected val voteRepoImpl = new VoteRepoImpl
  protected val accountRepoImpl = new AccountRepoImpl

  val webAPI = new WebAPI with TelegramRequestMock {
    override val customerRepo: CustomerRepo = customerRepoImpl
    override val photoRepo: PhotoRepo = photoRepoImpl
  }

  val telegramAPI = new TelegramAPI with TelegramRequestMock {
    override val photoRepo: PhotoRepo = photoRepoImpl
    override val voteRepo: VoteRepo = voteRepoImpl
    override val customerRepo: CustomerRepo = customerRepoImpl
  }

  val instagramFetcher = new InstagramFetcher with InstagramRequestMock {
    override val customerRepo: CustomerRepo = customerRepoImpl
    override val photoRepo: PhotoRepo = photoRepoImpl
    override val accountRepo: AccountRepo = accountRepoImpl
  }

}
