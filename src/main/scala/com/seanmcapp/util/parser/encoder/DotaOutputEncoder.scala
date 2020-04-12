package com.seanmcapp.util.parser.encoder

import com.seanmcapp.repository.dota.{Hero, Player}

case class MatchViewModel(matchId: Long, players: Seq[MatchPlayer], mode: String, startTime: String,
                          duration: String, side: String, result: String)

case class MatchPlayer(player: Player, hero: Hero, kill: Int, death: Int, assist: Int)

case class PlayerWinSummary(player: Player, win: Int, games: Int, percentage: Double)

case class HeroWinSummary(hero: Hero, win: Int, games: Int, percentage: Double)

case class HomePageResponse(matches: Seq[MatchViewModel], players: Seq[Player], heroes: Seq[Hero])

case class PlayerPageResponse(player: Player, heroes: Seq[HeroWinSummary], peers: Seq[PlayerWinSummary])

case class HeroPageResponse(hero: Option[Hero], players: Seq[PlayerWinSummary])

trait DotaOutputEncoder extends Encoder {

  implicit val playerFormat = jsonFormat5(Player)

  implicit val heroFormat = jsonFormat5(Hero)

  implicit val matchPlayerFormat = jsonFormat5(MatchPlayer)

  implicit val matchViewModelFormat = jsonFormat7(MatchViewModel)

  implicit val playerWinSummaryFormat = jsonFormat4(PlayerWinSummary)

  implicit val heroWinSummaryFormat = jsonFormat4(HeroWinSummary)

  implicit val homePageResponseFormat = jsonFormat3(HomePageResponse)

  implicit val playerPageResponseFormat = jsonFormat3(PlayerPageResponse)

  implicit val heroPageResponseFormat = jsonFormat2(HeroPageResponse)

}
