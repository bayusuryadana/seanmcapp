package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future

case class Hero(id: Int, localizedName: String, primaryAttr: String, image: String, lore: Option[String])

class HeroInfo(tag: Tag) extends Table[Hero](tag, "heroes") {
  val id = column[Int]("id", O.PrimaryKey)
  val localizedName = column[String]("localized_name")
  val primaryAttr = column[String]("primary_attr")
  val image = column[String]("image")
  val lore = column[Option[String]]("lore")

  def * = (id, localizedName, primaryAttr, image, lore) <> (Hero.tupled, Hero.unapply)
}

trait HeroRepo {

  def getAll: Future[Seq[Hero]]

  def get(id: Int): Future[Option[Hero]]

}

object HeroRepoImpl extends TableQuery(new HeroInfo(_)) with HeroRepo with DBComponent {

  def getAll: Future[Seq[Hero]] = {
    run(this.result)
  }

  def get(id: Int): Future[Option[Hero]] = {
    run(this.filter(_.id === id).result.headOption)
  }

}