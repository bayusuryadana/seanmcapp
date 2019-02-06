package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerInfo(tag: Tag) extends Table[Player](tag, "players") {
  val id = column[Int]("id", O.PrimaryKey)
  val realName = column[String]("realname")
  val avatarFull = column[String]("avatarfull")
  val personaName = column[String]("personaname")
  val MMREstimante = column[Int]("mmr_estimante")

  def * = (id, realName, avatarFull, personaName, MMREstimante) <> (Player.tupled, Player.unapply)
}

object PlayerRepoImpl extends TableQuery(new PlayerInfo(_)) with PlayerRepo with DBComponent {

  def getAll: Future[Set[Int]] = {
    run(this.map(_.id).result).map(_.toSet)
  }

}
