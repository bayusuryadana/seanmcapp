package com.seanmcapp.repository.mysql

import com.seanmcapp.repository.{Track, TrackRepo}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class TrackInfo(tag: Tag) extends Table[Track](tag, "tracks") {
  val customerId = column[Long]("customers_id", O.PrimaryKey)
  val photoId = column[Long]("photos_id", O.PrimaryKey)
  val date = column[Long]("date", O.PrimaryKey)

  def * = (customerId, photoId, date) <> (Track.tupled, Track.unapply)
  //def pk = primaryKey("pk", (customerId, photoId, date))
  //def customerFK = foreignKey("CUS_FK", customerId, new CustomerRepoImpl)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)
  //def photoFK = foreignKey("PHO_FK", photoId, new PhotoRepoImpl)(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Restrict)

}

class TrackRepoImpl extends TableQuery(new TrackInfo(_)) with TrackRepo with DBComponent {

  def insert(track: Track): Future[Int] = {
    run(this += track)
  }

  // TODO: get rid of this to Grafana
  def getAll: Future[Seq[Track]] = {
    run(this.result)
  }


}
