package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Match(id: Long, playersId: Int, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                  lobbyType: Int, heroId: Int, startTime: Int, kills: Int, deaths: Int, assists: Int)

class MatchInfo(tag: Tag) extends Table[Match](tag, "matches") {
  val id = column[Long]("id")
  val playersId = column[Int]("players_id")
  val playerSlot = column[Int]("player_slot")
  val radiantWin = column[Boolean]("radiant_win")
  val duration = column[Int]("duration")
  val gameMode = column[Int]("game_mode")
  val lobbyType = column[Int]("lobby_type")
  val heroId = column[Int]("hero_id")
  val startTime = column[Int]("start_time")
  val kills = column[Int]("kills")
  val deaths = column[Int]("deaths")
  val assists = column[Int]("assists")

  def * = (id, playersId, playerSlot, radiantWin, duration, gameMode, lobbyType, heroId, startTime, kills, deaths,
    assists) <> (Match.tupled, Match.unapply)

}

trait MatchRepo {

  def getAllMatchId: Future[Set[Long]]

  def insert(matches: Seq[Match]): Future[Option[Int]]

}

object MatchRepoImpl extends TableQuery(new MatchInfo(_)) with MatchRepo with DBComponent {

  def getAllMatchId: Future[Set[Long]] = run(this.map(_.id).result).map(_.toSet)

  def insert(matches: Seq[Match]): Future[Option[Int]] = run(this ++= matches)

}
