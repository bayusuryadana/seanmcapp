package com.seanmcapp.startup

import com.seanmcapp.api.{TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository.{AccountRepo, CustomerRepo, PhotoRepo, VoteRepo}
import com.seanmcapp.repository.mongodb.{AccountRepoImpl, CustomerRepoImpl, PhotoRepoImpl, VoteRepoImpl}

trait Injection {

  private val customerRepoImpl = new CustomerRepoImpl
  private val photoRepoImpl = new PhotoRepoImpl
  private val voteRepoImpl = new VoteRepoImpl
  private val accountRepoImpl = new AccountRepoImpl

  val webAPI = new WebAPI {
    override val customerRepo: CustomerRepo = customerRepoImpl
    override val photoRepo: PhotoRepo = photoRepoImpl
  }

  val telegramAPI = new TelegramAPI {
    override val photoRepo: PhotoRepo = photoRepoImpl
    override val voteRepo: VoteRepo = voteRepoImpl
    override val customerRepo: CustomerRepo = customerRepoImpl
  }

  val instagramFetcher = new InstagramFetcher {
    override val customerRepo: CustomerRepo = customerRepoImpl
    override val photoRepo: PhotoRepo = photoRepoImpl
    override val accountRepo: AccountRepo = accountRepoImpl
  }
}
