package com.seanmcapp.external

case class MatchResponse(match_id: Long, player_slot: Int, radiant_win: Boolean, duration: Int, game_mode: Int,
                         hero_id: Int, start_time: Long, kills: Int, deaths: Int, assists: Int) {

  def getWinStatus: String = if (player_slot < 100 ^ radiant_win) "Lose" else "Win"

}

case class PeerResponse(account_id: Int, win: Int, games:Int)

case class PlayerResponse(profile: ProfileResponse, rank_tier: Option[Int])

case class ProfileResponse(personaname: String, avatarfull: String)

case class HeroResponse(id: Int, localized_name: String, primary_attr: String, attack_type: String, roles: List[String],
                        img: String, icon: String)