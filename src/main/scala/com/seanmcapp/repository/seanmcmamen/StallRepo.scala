package com.seanmcapp.repository.seanmcmamen

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Stall(id: Int, name: String, placeCode: String, cityId: City, gmapsUrl: String, youtubeUrl: String,
                 latitude: Option[Double], longitude: Option[Double], placeId: Option[String])

object StallUtil {
  def apply(a: (Int, String, String, Int, String, String, Option[Double], Option[Double], Option[String])) =
    Stall(a._1, a._2, a._3, Cities.apply(a._4), a._5, a._6, a._7, a._8, a._9)
  def unapply(a: Stall) =
    Some(a.id, a.name, a.placeCode, a.cityId.i, a.gmapsUrl, a.youtubeUrl, a.latitude, a.longitude, a.placeId)
}

class StallInfo(tag: Tag) extends Table[Stall](tag, "stalls") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")
  val plusCode = column[String]("plus_code")
  val cityId = column[Int]("city_id")
  val gmapsUrl = column[String]("gmaps_url")
  val youtubeUrl = column[String]("youtube_url")
  val latitude = column[Option[Double]]("latitude")
  val longitude = column[Option[Double]]("longitude")
  val placeId = column[Option[String]]("place_id")

  def * =
    (id, name, plusCode, cityId, gmapsUrl, youtubeUrl, latitude, longitude, placeId) <>
      (StallUtil.apply, StallUtil.unapply)
}

trait StallRepo {

  def getAll: Future[Seq[Stall]]

}

object StallRepoImpl extends TableQuery(new StallInfo(_)) with StallRepo with DBComponent {
  
  override def getAll: Future[Seq[Stall]] = run(this.result)

}
