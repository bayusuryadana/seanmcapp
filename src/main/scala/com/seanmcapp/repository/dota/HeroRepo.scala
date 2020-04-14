package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class Hero(id: Int, localizedName: String, primaryAttr: String, attackType: String, roles: String, image: String, icon: String, lore: String)

class HeroInfo(tag: Tag) extends Table[Hero](tag, "heroes") {
  val id = column[Int]("id", O.PrimaryKey)
  val localizedName = column[String]("localized_name")
  val primaryAttr = column[String]("primary_attr")
  val attackType = column[String]("attack_type")
  val roles = column[String]("roles")
  val image = column[String]("image")
  val icon = column[String]("icon")
  val lore = column[String]("lore")

  def * = (id, localizedName, primaryAttr, attackType, roles, image, icon, lore) <> (Hero.tupled, Hero.unapply)
}

trait HeroRepo {

  def getAll: Future[Seq[Hero]]

  def get(id: Int): Future[Option[Hero]]

  def insertOrUpdate(heroes: Seq[Hero]): Seq[Future[Int]]

}

object HeroRepoImpl extends TableQuery(new HeroInfo(_)) with HeroRepo with DBComponent {

  def getAll: Future[Seq[Hero]] = {
    run(this.result)
  }

  def get(id: Int): Future[Option[Hero]] = {
    run(this.filter(_.id === id).result.headOption)
  }

  def insertOrUpdate(heroes: Seq[Hero]): Seq[Future[Int]] = {
    heroes.map { hero =>
      // try insert first
      run((this += hero).asTry).flatMap {
        case Failure(ex) =>
          // else try update
          run(this.filter(_.id === hero.id).update(hero))
        case Success(value) =>
          Future.successful(value)
      }
    }

  }
}