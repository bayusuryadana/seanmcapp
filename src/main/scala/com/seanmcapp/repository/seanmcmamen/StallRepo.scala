package com.seanmcapp.repository.seanmcmamen

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Stall(id: Int, name: String, placeCode: String, cityId: City, youtubeUrl: String, gmapsUrl: String, latitude: Option[Double], longitude: Option[Double], placeId: Option[String])

object StallUtil {
  def apply(a: (Int, String, String, Int, String, String, Double, Double, String)) = Stall(a._1, a._2, a._3, Cities.apply(a._4), a._5, a._6, Option(a._7), Option(a._8), Option(a._9))
  def unapply(a: Stall) = Some(a.id, a.name, a.placeCode, a.cityId.i, a.youtubeUrl, a.gmapsUrl, a.latitude.get, a.longitude.get, a.placeId.get)
}

class DinerInfo(tag: Tag) extends Table[Stall](tag, "stalls") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")
  val plusCode = column[String]("plus_code")
  val cityId = column[Int]("city_id")
  val youtubeUrl = column[String]("youtube_url")
  val gmapsUrl = column[String]("gmaps_url")
  val latitude = column[Double]("latitude")
  val longitude = column[Double]("longitude")
  val placeId = column[String]("place_id")
  
  def * = (id, name, plusCode, cityId, youtubeUrl, gmapsUrl, latitude, longitude, placeId) <> (StallUtil.apply, StallUtil.unapply)
}

trait StallRepo {
  
  def getAll: Future[Seq[Stall]]
  
}

object StallRepoImpl extends TableQuery(new DinerInfo(_)) with StallRepo with DBComponent {
  
  override def getAll: Future[Seq[Stall]] = run(this.result)

}
