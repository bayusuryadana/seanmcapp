package com.seanmcapp.repository.postgre

import com.seanmcapp.repository.{Track, TrackRepo}
import org.mongodb.scala.Completed
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class TrackInfo(tag: Tag) extends Table[Track](tag, "tracks") {
  val id = 0 // TODO: need to auto generate this
  val customerId = column[Long]("customers_id")
  val photoId = column[Long]("photos_id")
  val date = column[Long]("date")

  def * = (customerId, photoId, date) <> (Track.tupled, Track.unapply)

}

class TrackRepoImpl extends TableQuery(new TrackInfo(_)) with TrackRepo with DBComponent {

  def getAll: Future[Seq[Track]] = {
    run(this.result)
  }

  def getLast100(customerId: Long): Future[Set[Long]] = ??? // TODO: implement

  override def insert(track: Track): Future[Completed] = ??? // TODO: implement
}
