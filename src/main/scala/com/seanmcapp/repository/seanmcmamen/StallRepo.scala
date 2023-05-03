package com.seanmcapp.repository.seanmcmamen

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Stall(id: Int, name: String, description: String, address: String, cityId: City, latitude: Double, longitude: Double, youtubeUrl: String, gmapsUrl: String)

object StallUtil {
  def apply(a: (Int, String, String, String, Int, Double, Double, String, String)) = Stall(a._1, a._2, a._3, a._4, Cities.apply(a._5), a._6, a._7, a._8, a._9)
  def unapply(a: Stall) = Some(a.id, a.name, a.description, a.address, a.cityId.i, a.latitude, a.longitude, a.youtubeUrl, a.gmapsUrl)
}

class DinerInfo(tag: Tag) extends Table[Stall](tag, "stalls") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")
  val description = column[String]("description")
  val address = column[String]("address")
  val cityId = column[Int]("city_id")
  val latitude = column[Double]("latitude")
  val longitude = column[Double]("longitude")
  val youtubeUrl = column[String]("youtube_url")
  val gmapsUrl = column[String]("gmaps_url")
  
  def * = (id, name, description, address, cityId, latitude, longitude, youtubeUrl, gmapsUrl) <> (StallUtil.apply, StallUtil.unapply)
}

trait StallRepo {
  
  def getAll: Future[Seq[Stall]]
  
}

object StallRepoImpl extends TableQuery(new DinerInfo(_)) with StallRepo with DBComponent {
  
  override def getAll: Future[Seq[Stall]] = run(this.result)

}
