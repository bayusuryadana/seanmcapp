package com.seanmcapp.startup

import com.seanmcapp.api.{TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository.{CustomerRepo, PhotoRepo, VoteRepo}
import com.seanmcapp.repository.postgre._

trait Injection {

  protected val customerRepoImpl = new CustomerRepoImpl
  protected val photoRepoImpl = new PhotoRepoImpl
  protected val voteRepoImpl = new VoteRepoImpl
  protected val accRepoImpl = new AccountRepoImpl

  val webAPI = new WebAPI {
    override val customerRepo: CustomerRepo = customerRepoImpl
    override val photoRepo: PhotoRepo = photoRepoImpl
  }

  val telegramAPI = new TelegramAPI {
    override val photoRepo: PhotoRepo = photoRepoImpl
    override val voteRepo: VoteRepo = voteRepoImpl
    override val customerRepo: CustomerRepo = customerRepoImpl
  }

  val instagramFetcher = new InstagramFetcher(customerRepoImpl, photoRepoImpl)
}
