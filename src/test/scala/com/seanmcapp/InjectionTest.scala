package com.seanmcapp

import com.seanmcapp.api.{TelegramAPI, WebAPI}
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.repository.{CustomerRepo, Photo, PhotoRepo, VoteRepo}
import com.seanmcapp.repository.postgre.{AccountRepoImpl, CustomerRepoImpl, PhotoRepoImpl, VoteRepoImpl}

import scala.concurrent.Future

trait InjectionTest {

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
    override val photoRepo: PhotoRepo = new PhotoRepoImpl {
      override def update(photo: Photo): Future[Option[Photo]] = {
        Future.successful(None)
      }
    }
    override val instagramAccounts = List(("ui.cantik", "[\\w ]+\\. [\\w ]+['’]\\d\\d".r, "1435973343"))
  }

}
