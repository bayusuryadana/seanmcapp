package com.seanmcapp.util.parser.decoder

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                         heroId: Int, startTime: Long, kills: Int, deaths: Int, assists: Int, startTimeString: Option[String]) {

  def getWinStatus: String = if (playerSlot < 100 ^ radiantWin) "Lose" else "Win"

  def getDuration: String = (duration / 60) + ":" + (duration % 60)

  def formatStartTime: MatchResponse = {
    val date = new Date(this.startTime.toLong * 1000L)
    val fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm")
    fmt.setTimeZone(TimeZone.getTimeZone("GMT+7"))
    val startTimeString = fmt.format(date.getTime)
    this.copy(startTimeString = Some(startTimeString))
  }

//  def getGameMode: String = {
//    gameMode match {
//      case 1 => "All Pick"
//      case 2 => "Captain's Mode"
//      case 4 => "Single Draft"
//      case 5 => "All Random"
//      case 22 => "Ranked All Pick"
//      case 23 => "Turbo"
//      case _ => "Unknown"
//    }
//  }
}

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

trait DotaInputDecoder extends JsonDecoder {

  implicit val matchFormat = jsonFormat(MatchResponse, "match_id", "player_slot", "radiant_win", "duration", "game_mode",
    "hero_id", "start_time", "kills", "deaths", "assists", "start_time_string")

  implicit val peerFormat = jsonFormat(PeerResponse, "account_id", "win", "games")

}
