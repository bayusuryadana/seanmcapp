package com.seanmcapp.util.viewbuilder

import com.seanmcapp.repository.dota.{Hero, Player}
import org.joda.time.DateTime

import scala.io.Source

case class MatchViewModel(name: String, matchId: Long, side: String, result: String, duration: String, mode: String,
                          hero: String, startTime: DateTime, kda: String)

case class PlayerViewModel(name: String, personaName: String, mmrEstimate: Int)

case class PeerViewModel(peerName: String, win: Int, percentage:Double)

case class HeroViewModel(id: Int, localizedName: String, primaryAttr: String, attackType: String)

trait DotaViewBuilder {
  //TODO: this Trait is not yet implemented
  private val tr = "<tr>"
  private val tre = "</tr>"
  private val td = "<td>"
  private val tde = "</td>"

  def buildHomeView(matches: Seq[MatchViewModel], players: Seq[PlayerViewModel], heroes: Seq[HeroViewModel]): String = {
    templateSource("dota/home.html")
  }

  def buildPlayerView(player: Player, matches: Seq[MatchViewModel], peers: Seq[PeerViewModel]): String = {
    templateSource("dota/player.html")
  }

  def buildHeroView(hero: Hero, matches: Seq[MatchViewModel]): String = {
    templateSource("dota/hero.html")
  }

  private def templateSource(source: String): String = Source.fromResource(source).mkString
}
