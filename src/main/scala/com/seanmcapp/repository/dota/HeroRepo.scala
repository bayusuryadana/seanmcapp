package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

case class Hero(id: Int, localizedName: String, primaryAttr: String, image: String, lore: String)

object Hero {
  def apply(id: Int): Hero = Hero(id, "Unknown", "???", "", "")
}

class HeroInfo(tag: Tag) extends Table[Hero](tag, "heroes") {
  val id = column[Int]("id", O.PrimaryKey)
  val localizedName = column[String]("localized_name")
  val primaryAttr = column[String]("primary_attr")
  val image = column[String]("image")
  val lore = column[String]("lore")

  def * = (id, localizedName, primaryAttr, image, lore) <>
    ({case ((x1, x2, x3, x4, x5)) => Hero.apply(x1, x2, x3, x4, x5)}, Hero.unapply)
}

trait HeroRepo {

  def getAll: Future[List[Hero]]

  def get(id: Int): Future[Option[Hero]]

}

object HeroRepoImpl extends TableQuery(new HeroInfo(_)) with HeroRepo with DBComponent {

  def getAll: Future[List[Hero]] = {
    run(this.result)
  }

  def get(id: Int): Future[Option[Hero]] = {
    run(this.filter(_.id === id).result.headOption)
  }

}