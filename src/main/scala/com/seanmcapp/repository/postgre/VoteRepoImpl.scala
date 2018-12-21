package com.seanmcapp.repository.postgre

import com.seanmcapp.repository.{Vote, VoteRepo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class VoteInfo(tag: Tag) extends Table[Vote](tag, "votes") {
  val customerId = column[Long]("customers_id")
  val photoId = column[Long]("photos_id")
  val rating = column[Long]("rating")

  def * = (customerId, photoId, rating) <> (Vote.tupled, Vote.unapply)
}

class VoteRepoImpl extends TableQuery(new VoteInfo(_)) with VoteRepo with DBComponent {

  def getAll: Future[Seq[Vote]] = {
    run(this.result)
  }

  def update(vote: Vote): Future[Option[Vote]] = {
    run(this.returning(this).insertOrUpdate(vote))
  }

}
