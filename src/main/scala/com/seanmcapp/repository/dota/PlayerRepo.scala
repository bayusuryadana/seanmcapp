package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

case class Player(id: Int, realName: String, avatarFull: String, personaName: String, MMREstimate: Int)

class PlayerInfo(tag: Tag) extends Table[Player](tag, "players") {
  val id = column[Int]("id", O.PrimaryKey)
  val realName = column[String]("realname")
  val avatarFull = column[String]("avatarfull")
  val personaName = column[String]("personaname")
  val MMREstimante = column[Int]("mmr_estimante")

  def * = (id, realName, avatarFull, personaName, MMREstimante) <> (Player.tupled, Player.unapply)
}

trait PlayerRepo {

  def getAll: Future[Seq[Player]]

  def get(id: Int): Future[Option[Player]]

}

object PlayerRepoImpl extends TableQuery(new PlayerInfo(_)) with PlayerRepo with DBComponent {

  def getAll: Future[Seq[Player]] = {
    run(this.result)
  }

  def get(id: Int): Future[Option[Player]] = {
    run(this.filter(_.id === id).result.headOption)
  }

}
