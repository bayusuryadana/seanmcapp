package com.seanmcapp.util.parser.decoder

import com.seanmcapp.repository.dota.Player

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                         heroId: Int, startTime: Int, kills: Int, deaths: Int, assists: Int) {

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

case class MatchResponseWithPlayer(player: Player, mr: MatchResponse)

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

trait DotaInputDecoder extends Decoder {

  implicit val matchFormat = jsonFormat(MatchResponse, "match_id", "player_slot", "radiant_win", "duration", "game_mode",
    "hero_id", "start_time", "kills", "deaths", "assists")

  implicit val peerFormat = jsonFormat(PeerResponse, "account_id", "win", "games")

}
