package com.seanmcapp.util.viewbuilder

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import com.seanmcapp.repository.dota.{Hero, Player}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.io.Source

case class MatchViewModel(matchId: Long, name: String, hero: String, kda: String, mode: String, startTime: DateTime,
                          duration: String, side: String, result: String)

case class PlayerViewModel(id:Int, name: String, personaName: String, avatar: String)

case class PeerViewModel(peerName: String, win: Int, games: Int, percentage:Double)

case class HeroViewModel(id: Int, localizedName: String, primaryAttr: String, image: String, lore: String)

trait DotaViewBuilder {

  private val baseURL = "https://seanmcapp.herokuapp.com/dota"

  private val heroImageBaseURL = "https://api.opendota.com/apps/dota2/images/heroes/"

  def buildHomeView(matches: Seq[MatchViewModel], players: Seq[PlayerViewModel],
                    heroes: Seq[HeroViewModel]): HttpEntity.Strict = {
    val matchViewModel = matches.foldLeft("") { (res, m) =>
      res + templateSource("dota/home_match_item.html")
        .replace("{{match_id}}", m.matchId.toString)
        .replace("{{match_name}}", m.name)
        .replace("{{match_hero}}", m.hero)
        .replace("{{match_kda}}", m.kda)
        .replace("{{match_mode}}", m.mode)
        .replace("{{match_start_time}}", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm").print(m.startTime))
        .replace("{{match_duration}}", m.duration)
        .replace("{{match_side}}", m.side)
        .replace("{{match_result}}", m.result)
    }

    val playerViewModel = players.foldLeft("") { (itemString, p) =>
      itemString + templateSource("dota/home_player_item.html")
        .replace("{{player_url}}", baseURL + "/player/" + p.id)
        .replace("{{player_avatar}}", p.avatar)
        .replace("{{player_name}}", p.name)
        .replace("{{player_persona_name}}", p.personaName)
    }

    val heroViewModel = heroes.foldLeft("") { (itemString, h) =>
      itemString + templateSource("dota/home_hero_item.html")
        .replace("{{hero_url}}", baseURL + "/hero/" + h.id)
        .replace("{{image_url}}", heroImageBaseURL + h.image)
    }

    val res = templateSource("dota/home.html")
      .replace("{{base_url}}", baseURL)
      .replace("{{matchViewModel}}", matchViewModel)
      .replace("{{playerViewModel}}", playerViewModel)
      .replace("{{heroViewModel}}", heroViewModel)

    HttpEntity(ContentTypes.`text/html(UTF-8)`, res)
  }

  def buildPlayerView(player: Player, peerHero: Seq[PeerViewModel], peers: Seq[PeerViewModel]): HttpEntity.Strict = {

    val peerViewModel = peers.foldLeft("") { (res, p) =>
      res + templateSource("dota/player_peer_item.html")
        .replace("{{peer_name}}", p.peerName)
        .replace("{{peer_win}}", p.win.toString)
        .replace("{{peer_games}}", p.games.toString)
        .replace("{{peer_percentage}}", p.percentage.toString)
    }

    val peerHeroViewModel = peerHero.foldLeft("") { (res, p) =>
      res + templateSource("dota/player_peer_item.html")
        .replace("{{peer_name}}", p.peerName)
        .replace("{{peer_win}}", p.win.toString)
        .replace("{{peer_games}}", p.games.toString)
        .replace("{{peer_percentage}}", p.percentage.toString)
    }

    val res = templateSource("dota/player.html")
      .replace("{{player_avatar}}", player.avatarFull)
      .replace("{{player_name}}", player.realName)
      .replace("{{player_persona_name}}", player.personaName)
      .replace("{{peerViewModel}}", peerViewModel)
      .replace("{{peerHeroViewModel}}", peerHeroViewModel)

    HttpEntity(ContentTypes.`text/html(UTF-8)`, res)
  }

  def buildHeroView(hero: Hero, matches: Seq[PeerViewModel]): HttpEntity.Strict = {
    val peerViewModel = matches.foldLeft("") { (res, p) =>
      res + templateSource("dota/player_peer_item.html")
        .replace("{{peer_name}}", p.peerName)
        .replace("{{peer_win}}", p.win.toString)
        .replace("{{peer_games}}", p.games.toString)
        .replace("{{peer_percentage}}", p.percentage.toString)
    }

    val res = templateSource("dota/hero.html")
      .replace("{{hero_name}}", hero.localizedName)
      .replace("{{hero_image}}", heroImageBaseURL + hero.image)
      .replace("{{hero_lore}}", hero.lore)
      .replace("{{peerViewModel}}", peerViewModel)

    HttpEntity(ContentTypes.`text/html(UTF-8)`, res)
  }

  private def templateSource(source: String): String = Source.fromResource(source).mkString
}
