package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.Future

case class Hero(id: Int, localizedName: String, primaryAttr: String, attackType: String)

class HeroInfo(tag: Tag) extends Table[Hero](tag, "heroes") {
  val id = column[Int]("id", O.PrimaryKey)
  val localizedName = column[String]("localized_name")
  val primaryAttr = column[String]("primary_attr")
  val attackType = column[String]("attack_type")

  def * = (id, localizedName, primaryAttr, attackType) <> (Hero.tupled, Hero.unapply)
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