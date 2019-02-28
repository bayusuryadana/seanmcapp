package com.seanmcapp.mock.repository

import com.seanmcapp.repository.instagram.{Vote, VoteRepo}

import scala.concurrent.Future

object VoteRepoMock extends VoteRepo {

  private val voteList = Seq(
    Vote(-111546505, 772020198343721705L, 1),
    Vote(-209240150, 990599194820723882L, 2),
    Vote(26694991, 772020198343721705L, 3),
    Vote(26694991, 956621307316650190L, 4),
    Vote(123, 784771576786862732L, 5)
  )

  override def getAll: Future[Seq[Vote]] = Future.successful(voteList)

  override def insertOrUpdate(vote: Vote): Future[Int] = Future.successful(1)

}
