package com.seanmcapp.util.parser.encoder

import com.seanmcapp.repository.dota.{Hero, HeroAttribute, Player}
import com.seanmcapp.util.parser.decoder.MatchResponse

case class WinSummary(win: Int, games: Int, percentage: Double, rating: Option[Double])

case class PlayerInfo(player:Player, winSummary: WinSummary, matches: Seq[MatchResponse], topHero: Seq[(Hero, WinSummary)])

case class HeroInfo(hero: Hero, heroAttribute: HeroAttribute, topPlayer: Seq[(Player, WinSummary)])

case class HomePageResponse(players: Seq[PlayerInfo], heroes: Seq[HeroInfo])

trait DotaOutputEncoder extends Encoder {

  implicit val playerFormat = jsonFormat5(Player)

  implicit val heroFormat = jsonFormat8(Hero.apply)

  implicit val heroAttributeFormat = jsonFormat21(HeroAttribute.apply)

  implicit val matchFormat = jsonFormat10(MatchResponse)

  implicit val winSummaryFormat = jsonFormat4(WinSummary)

  implicit val playerInfoFormat = jsonFormat4(PlayerInfo)

  implicit val heroInfoFormat = jsonFormat3(HeroInfo)

  implicit val homePageResponseFormat = jsonFormat2(HomePageResponse)

}
