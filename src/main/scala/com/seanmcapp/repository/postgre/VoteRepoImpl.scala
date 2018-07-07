package com.seanmcapp.repository.postgre

import com.seanmcapp.repository.{Vote, VoteRepo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class VoteInfo(tag: Tag) extends Table[Vote](tag, "votes") {
  val id = column[String]("id", O.PrimaryKey)
  val photoId = column[String]("photos_id")
  val customerId = column[Long]("customers_id")
  val rating = column[Long]("rating")

  def * = (id, photoId, customerId, rating) <> (Vote.tupled, Vote.unapply)
}

class VoteRepoImpl extends TableQuery(new VoteInfo(_)) with VoteRepo with DBComponent {

  def update(vote: Vote): Future[Option[Vote]] = {
    run(this.returning(this).insertOrUpdate(vote))
  }

}
