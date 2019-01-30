package com.seanmcapp.repository.mysql

import com.seanmcapp.repository.{Track, TrackRepo}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class TrackInfo(tag: Tag) extends Table[Track](tag, "tracks") {
  val customerId = column[Long]("customers_id")
  val photoId = column[Long]("photos_id")
  val date = column[Long]("date")

  def * = (customerId, photoId, date) <> (Track.tupled, Track.unapply)
  def pk = primaryKey("pk", (customerId, photoId, date))
  def customerFK = foreignKey("CUS_FK", customerId, CustomerRepoImpl)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
  def photoFK = foreignKey("PHO_FK", photoId, PhotoRepoImpl)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)

}

object TrackRepoImpl extends TableQuery(new TrackInfo(_)) with TrackRepo with DBComponent {

  def update(track: Track): Future[Option[Track]] = {
    run(this.returning(this).insertOrUpdate(track))
  }

  // TODO: get rid of this to Join
  def getAll: Future[Seq[Track]] = {
    run(this.result)
  }


}
