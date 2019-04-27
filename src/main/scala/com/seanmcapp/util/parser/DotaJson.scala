package com.seanmcapp.util.parser

import com.seanmcapp.repository.dota.{Hero, Player}
import com.seanmcapp.service._
import org.joda.time.DateTime
import spray.json._

case class ArrayResponse[T](res: Seq[T])

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                          lobbyType: Int, heroId: Int, startTime: Int, kills: Int, deaths: Int, assists: Int) {

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

  def getLobbyType: String = {
    lobbyType match {
      case 0 => "Normal"
      case 5 => "Ranked Team"
      case 6 => "Ranked Solo"
      case 7 => "Ranked Party"
    }
  }

}

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

object DotaJson extends DefaultJsonProtocol {

  implicit val playerFormat = jsonFormat4(Player)

  implicit val heroFormat = jsonFormat5(Hero)

  implicit val matchFormat = jsonFormat(MatchResponse, "match_id", "player_slot", "radiant_win", "duration", "game_mode",
    "lobby_type", "hero_id", "start_time", "kills", "deaths", "assists")

  implicit val matchesFormat = new RootJsonFormat[ArrayResponse[MatchResponse]] {
    def read(value: JsValue) = ArrayResponse(value.convertTo[Seq[MatchResponse]])
    def write(obj: ArrayResponse[MatchResponse]) = obj.res.toJson
  }

  implicit val peerFormat = jsonFormat(PeerResponse, "account_id", "win", "games")

  implicit val peersFormat = new RootJsonFormat[ArrayResponse[PeerResponse]] {
    def read(value: JsValue) = ArrayResponse(value.convertTo[Seq[PeerResponse]])
    def write(obj: ArrayResponse[PeerResponse]) = obj.res.toJson
  }

  implicit val matchViewModelFormat = jsonFormat9(MatchViewModel)

  implicit val winSummaryFormat = jsonFormat4(WinSummary)

  implicit val homePageResponseFormat = jsonFormat3(HomePageResponse)

  implicit val playerPageResponseFormat = jsonFormat3(PlayerPageResponse)

  implicit val heroPageResponseFormat = jsonFormat2(HeroPageResponse)

}
