package com.seanmcapp.util.parser

import com.seanmcapp.repository.dota.{Hero, Player}
import com.seanmcapp.service._
import spray.json._

case class ArrayResponse[T](res: Seq[T])

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                         heroId: Int, startTime: Int, kills: Int, deaths: Int, assists: Int, playerId: Option[Long]) {

  def appendId(id: Long): MatchResponse = this.copy(playerId = Some(id))

  def getSide: String = if (playerSlot < 100) "Radiant" else "Dire"

  def getWinStatus: String = if (playerSlot < 100 ^ radiantWin) "Lose" else "Win"

  def getDuration: String = (duration / 60) + ":" + (duration % 60)

  def getGameMode: String = {
    gameMode match {
      case 1 => "All Pick"
      case 2 => "Captain's Mode"
      case 4 => "Single Draft"
      case 5 => "All Random"
      case 22 => "Ranked All Pick"
      case 23 => "Turbo"
      case _ => "Unknown"
    }
  }

}

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

object DotaOutputJson extends DefaultJsonProtocol {

  implicit val playerFormat = jsonFormat5(Player)

  implicit val heroFormat = jsonFormat5(Hero)

  implicit val matchFormat = jsonFormat(MatchResponse, "match_id", "player_slot", "radiant_win", "duration", "game_mode",
    "hero_id", "start_time", "kills", "deaths", "assists", "player_id")

  implicit val matchesFormat = new RootJsonFormat[ArrayResponse[MatchResponse]] {
    def read(value: JsValue) = ArrayResponse(value.convertTo[Seq[MatchResponse]])
    def write(obj: ArrayResponse[MatchResponse]) = obj.res.toJson
  }

  implicit val peerFormat = jsonFormat(PeerResponse, "account_id", "win", "games")

  implicit val peersFormat = new RootJsonFormat[ArrayResponse[PeerResponse]] {
    def read(value: JsValue) = ArrayResponse(value.convertTo[Seq[PeerResponse]])
    def write(obj: ArrayResponse[PeerResponse]) = obj.res.toJson
  }

  implicit val matchPlayerFormat = jsonFormat3(MatchPlayer)

  implicit val matchViewModelFormat = jsonFormat7(MatchViewModel)

  implicit val winSummaryFormat = jsonFormat4(WinSummary)

  implicit val homePageResponseFormat = jsonFormat3(HomePageResponse)

  implicit val playerPageResponseFormat = jsonFormat3(PlayerPageResponse)

  implicit val heroPageResponseFormat = jsonFormat2(HeroPageResponse)

}
