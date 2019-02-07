package com.seanmcapp.util.parser

import com.seanmcapp.repository.dota.{Match, Peer}
import spray.json._

case class ArrayResponse[T](res: Seq[T])

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                          lobbyType: Int, heroId: Int, startTime: Int, kills: Int, deaths: Int, assists: Int) {

  def toMatch(playerId: Int): Match = {
    Match(
      matchId, playerId, playerSlot, radiantWin,
      duration, gameMode, lobbyType, heroId, startTime,
      kills, deaths, assists
    )
  }

}

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int) {
  def toPeer(playerId: Int): Peer = {
    Peer(playerId, peerPlayerId, win, games)
  }
}

object DotaJson extends DefaultJsonProtocol {

  implicit val matchFormat = jsonFormat(MatchResponse, "match_id", "player_slot", "radiant_win", "duration", "game_mode",
    "lobby_type", "hero_id", "start_time", "kills", "deaths", "assists")

  implicit val matchesFormat = new RootJsonFormat[ArrayResponse[MatchResponse]] {
    def read(value: JsValue) = ArrayResponse(value.convertTo[List[MatchResponse]])
    def write(obj: ArrayResponse[MatchResponse]) = obj.res.toJson
  }

  implicit val peerFormat = jsonFormat(PeerResponse, "account_id", "win", "games")

  implicit val peersFormat = new RootJsonFormat[ArrayResponse[PeerResponse]] {
    def read(value: JsValue) = ArrayResponse(value.convertTo[List[PeerResponse]])
    def write(obj: ArrayResponse[PeerResponse]) = obj.res.toJson
  }

}
