package com.seanmcapp.repository.mysql

import com.seanmcapp.repository.{Track, TrackRepo}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class TrackInfo(tag: Tag) extends Table[Track](tag, "tracks") {
  val customerId = column[Long]("customers_id")
  val photoId = column[Long]("photos_id")
  val date = column[Long]("date")

  def * = (customerId, photoId, date) <> (Track.tupled, Track.unapply)

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
