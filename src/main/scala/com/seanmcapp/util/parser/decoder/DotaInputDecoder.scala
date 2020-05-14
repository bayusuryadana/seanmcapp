package com.seanmcapp.util.parser.decoder

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                         heroId: Int, startTime: Long, kills: Int, deaths: Int, assists: Int) {

  def getWinStatus: String = if (playerSlot < 100 ^ radiantWin) "Lose" else "Win"

}

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

trait DotaInputDecoder extends JsonDecoder {

  implicit val matchFormat = jsonFormat(MatchResponse, "match_id", "player_slot", "radiant_win", "duration", "game_mode",
    "hero_id", "start_time", "kills", "deaths", "assists")

  implicit val peerFormat = jsonFormat(PeerResponse, "account_id", "win", "games")

}
