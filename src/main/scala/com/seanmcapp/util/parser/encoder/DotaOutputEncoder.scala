package com.seanmcapp.util.parser.encoder

import com.seanmcapp.repository.dota.{Hero, Player}

case class MatchViewModel(matchId: Long, players: Seq[MatchPlayer], mode: String, startTime: String,
                          duration: String, side: String, result: String)

case class MatchPlayer(name: String, hero: String, kda: String)

case class WinSummary(peerName: String, win: Int, games: Int, percentage:Double)

case class HomePageResponse(matches: Seq[MatchViewModel], players: Seq[Player], heroes: Seq[Hero])

case class PlayerPageResponse(player: Player, heroes: Seq[WinSummary], peers: Seq[WinSummary])

case class HeroPageResponse(hero: Hero, players: Seq[WinSummary])

trait DotaOutputEncoder extends Encoder {

  implicit val playerFormat = jsonFormat5(Player)

  implicit val heroFormat = jsonFormat5(Hero)

  implicit val matchPlayerFormat = jsonFormat3(MatchPlayer)

  implicit val matchViewModelFormat = jsonFormat7(MatchViewModel)

  implicit val winSummaryFormat = jsonFormat4(WinSummary)

  implicit val homePageResponseFormat = jsonFormat3(HomePageResponse)

  implicit val playerPageResponseFormat = jsonFormat3(PlayerPageResponse)

  implicit val heroPageResponseFormat = jsonFormat2(HeroPageResponse)

}
