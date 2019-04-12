package com.seanmcapp.util.viewbuilder

import com.seanmcapp.repository.dota.{Hero, Player}
import org.joda.time.DateTime

import scala.io.Source

case class MatchViewModel(matchId: Long, name: String, hero: String, kda: String, mode: String, startTime: DateTime,
                          duration: String, side: String, result: String)

case class PlayerViewModel(name: String, personaName: String, mmrEstimate: Int)

case class PeerViewModel(peerName: String, win: Int, percentage:Double)

case class HeroViewModel(id: Int, localizedName: String, primaryAttr: String, attackType: String)

trait DotaViewBuilder {
  //TODO: this Trait is not yet implemented
  private val tr = "<tr>"
  private val tre = "</tr>"
  private val td = "<td>"
  private val tde = "</td>"

  def buildKontol: String = templateSource("dota/home.html")

  def buildHomeView(matches: Seq[MatchViewModel], players: Seq[PlayerViewModel], heroes: Seq[HeroViewModel]): String = {
    val matchViewModel = matches.foldLeft("") { (res, m) =>
      res + tr + m.getClass.getDeclaredFields.map { f =>
        f.setAccessible(true)
        td + f.get(m) + tde
      } + tre
    }

    val playerViewModel = ""

    val heroViewModel = ""

    templateSource("dota/home.html")
      .replace("${matchViewModel}", matchViewModel)
      .replace("${playerViewModel}", playerViewModel)
      .replace("${heroViewModel}", heroViewModel)
  }

  def buildPlayerView(player: Player, matches: Seq[MatchViewModel], peers: Seq[PeerViewModel]): String = {
    templateSource("dota/player.html")
  }

  def buildHeroView(hero: Hero, matches: Seq[MatchViewModel]): String = {
    templateSource("dota/hero.html")
  }

  private def templateSource(source: String): String = Source.fromResource(source).mkString
}
