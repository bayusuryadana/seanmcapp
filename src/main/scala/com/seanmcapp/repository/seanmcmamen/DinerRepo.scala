package com.seanmcapp.repository.seanmcmamen

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Diner(id: Int, name: String, description: String, address: String, cityId: City, latitude: Double, longitude: Double, youtubeUrl: String)

object DinerUtil {
  def apply(a: (Int, String, String, String, Int, Double, Double, String)) = Diner(a._1, a._2, a._3, a._4, Cities.apply(a._5), a._6, a._7, a._8)
  def unapply(a: Diner) = Some(a.id, a.name, a.description, a.address, a.cityId.i, a.latitude, a.longitude, a.youtubeUrl)
}

class DinerInfo(tag: Tag) extends Table[Diner](tag, "diners") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")
  val description = column[String]("description")
  val address = column[String]("address")
  val cityId = column[Int]("city_id")
  val latitude = column[Double]("latitude")
  val longitude = column[Double]("longitude")
  val youtubeUrl = column[String]("youtube_url")
  
  def * = (id, name, description, address, cityId, latitude, longitude, youtubeUrl) <> (DinerUtil.apply, DinerUtil.unapply)
}

trait DinerRepo {
  
  def getAll: Future[Seq[Diner]]
  
}

object DinerRepoImpl extends TableQuery(new DinerInfo(_)) with DinerRepo with DBComponent {
  
  override def getAll: Future[Seq[Diner]] = run(this.result)

}
