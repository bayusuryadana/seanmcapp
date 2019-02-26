package com.seanmcapp

import com.seanmcapp.repository.instagram._
import com.seanmcapp.service._

trait InjectionTest {

  private val customerRepoImpl = CustomerRepoImpl
  private val photoRepoImpl = PhotoRepoImpl
  private val voteRepoImpl = VoteRepoImpl
  private val trackRepoImpl = TrackRepoImpl

  trait ServiceImpl extends Service {
    override val customerRepo = customerRepoImpl
    override val photoRepo = photoRepoImpl
    override val voteRepo = voteRepoImpl
    override val trackRepo = trackRepoImpl
  }

  val webService = new WebService with ServiceImpl

  val telegramService = new TelegramService with ServiceImpl

}
