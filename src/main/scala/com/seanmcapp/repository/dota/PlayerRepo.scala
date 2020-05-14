package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Player(id: Int, realName: String, avatarFull: String, personaName: String, rankTier: Option[Int])

class PlayerInfo(tag: Tag) extends Table[Player](tag, "players") {
  val id = column[Int]("id", O.PrimaryKey)
  val realName = column[String]("realname")
  val avatarFull = column[String]("avatarfull")
  val personaName = column[String]("personaname")
  val rankTier = column[Option[Int]]("rank_tier")

  def * = (id, realName, avatarFull, personaName, rankTier) <> (Player.tupled, Player.unapply)
}

trait PlayerRepo {

  def getAll: Future[Seq[Player]]

  def update(player: Player): Future[Int]

}

object PlayerRepoImpl extends TableQuery(new PlayerInfo(_)) with PlayerRepo with DBComponent {

  def getAll: Future[Seq[Player]] = {
    run(this.result)
  }

  def update(player: Player): Future[Int] = {
    run(this.filter(_.id === player.id).update(player))
  }

}
