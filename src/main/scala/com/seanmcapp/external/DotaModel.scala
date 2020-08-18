package com.seanmcapp.external

case class MatchResponse(matchId: Long, playerSlot: Int, radiantWin: Boolean, duration: Int, gameMode: Int,
                         heroId: Int, startTime: Long, kills: Int, deaths: Int, assists: Int) {

  def getWinStatus: String = if (playerSlot < 100 ^ radiantWin) "Lose" else "Win"

}

case class PeerResponse(peerPlayerId: Int, win: Int, games:Int)

case class PlayerResponse(profile: ProfileResponse, rankTier: Option[Int])

case class ProfileResponse(personaName: String, avatarfull: String)

case class HeroResponse(id: Int, localizedName: String, primaryAttr: String, attackType: String, roles: List[String],
                        img: String, icon: String)