package com.seanmcapp.repository.mysql

import com.seanmcapp.repository.{Vote, VoteRepo}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class VoteInfo(tag: Tag) extends Table[Vote](tag, "votes") {
  val customerId = column[Long]("customers_id")
  val photoId = column[Long]("photos_id")
  val rating = column[Long]("rating")

  def * = (customerId, photoId, rating) <> (Vote.tupled, Vote.unapply)
  def pk = primaryKey("pk", (customerId, photoId))
  def customerFK = foreignKey("CUS_FK", customerId, new CustomerRepoImpl)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
  def photoFK = foreignKey("PHO_FK", photoId, new PhotoRepoImpl)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
}

class VoteRepoImpl extends TableQuery(new VoteInfo(_)) with VoteRepo with DBComponent {

  def update(vote: Vote): Future[Option[Vote]] = {
    run(this.returning(this).insertOrUpdate(vote))
  }

  // TODO: get rid of this to Join
  def getAll: Future[Seq[Vote]] = {
    run(this.result)
  }

}
