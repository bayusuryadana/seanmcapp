package com.seanmcapp.repository.dota

import scala.concurrent.Future

case class Match(
                  id: Long,
                  playersId: Int,
                  playerSlot: Int,
                  radiantWin: Boolean,
                  duration: Int,
                  gameMode: Int,
                  lobbyType: Int,
                  heroId: Int,
                  startTime: Int,
                  kills: Int,
                  deaths: Int,
                  assists: Int
                )

trait MatchRepo {

  def getAllMatchId: Future[Set[Long]]

  def insert(matches: Seq[Match]): Future[Option[Int]]

}
