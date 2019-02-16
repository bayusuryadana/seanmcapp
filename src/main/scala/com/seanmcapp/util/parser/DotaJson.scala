package com.seanmcapp.util.parser

import com.seanmcapp.repository.dota.{Hero, Player}
import spray.json._

case class ArrayResponse[T](res: Seq[T])

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                          lobbyType: Int, heroId: Int, startTime: Int, kills: Int, deaths: Int, assists: Int)

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

object DotaJson extends DefaultJsonProtocol {

  implicit val playerFormat = jsonFormat5(Player)

  implicit val heroFormat = jsonFormat4(Hero)

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

}
