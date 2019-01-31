package com.seanmcapp.repository.mysql

import com.seanmcapp.repository.{Vote, VoteRepo}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class VoteInfo(tag: Tag) extends Table[Vote](tag, "votes") {
  val customerId = column[Long]("customers_id")
  val photoId = column[Long]("photos_id")
  val rating = column[Long]("rating")

  def * = (customerId, photoId, rating) <> (Vote.tupled, Vote.unapply)
}

class VoteRepoImpl extends TableQuery(new VoteInfo(_)) with VoteRepo with DBComponent {

  def insertOrUpdate(vote: Vote): Future[Int] = {
    val q = this.filter(_.customerId === vote.customerId).filter(_.photoId === vote.photoId)
    for {
      length <- run(q.length.result)
      affectedRow <- run(if (length > 0) q.update(vote) else this += vote)
    } yield {
      affectedRow
    }
  }

  // TODO: get rid of this to Grafana
  def getAll: Future[Seq[Vote]] = {
    run(this.result)
  }

}
