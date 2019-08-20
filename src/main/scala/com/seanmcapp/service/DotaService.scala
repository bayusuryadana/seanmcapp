package com.seanmcapp.service

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import com.seanmcapp.repository.dota.{Hero, HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class MatchViewModel(matchId: Long, players: Seq[MatchPlayer], mode: String, startTime: String,
                          duration: String, side: String, result: String)

case class MatchPlayer(name: String, hero: String, kda: String)

case class WinSummary(peerName: String, win: Int, games: Int, percentage:Double)

case class HomePageResponse(matches: Seq[MatchViewModel], players: Seq[Player], heroes: Seq[Hero])

case class PlayerPageResponse(player: Player, heroes: Seq[WinSummary], peers: Seq[WinSummary])

case class HeroPageResponse(hero: Hero, players: Seq[WinSummary])

trait DotaService extends DotaRequestBuilder {

  val playerRepo: PlayerRepo
  val heroRepo: HeroRepo

  def home: Future[HomePageResponse] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val matchViewModels = players.flatMap(getMatches).groupBy(_.startTime).toSeq.sortBy(-_._1).map(_._2).take(10).flatMap { identicalMatches =>
        identicalMatches.headOption.map { matchHead =>
          val matchPlayerList = identicalMatches.map { m =>
            val hero = heroes.find(_.id == m.heroId).map(_.localizedName).getOrElse("Unknown")
            toMatchPlayer(m, m.player.personaName, hero)
          }
          toMatchViewModel(matchHead, matchPlayerList)
        }
      }
      HomePageResponse(matchViewModels, players, heroes.map(hero => hero.copy(lore = "")))
    }
  }

  def player(id: Int): Future[PlayerPageResponse] = {
    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll
    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val player = players.find(_.id == id).getOrElse(throw new Exception("Player not found")) // TODO: should response to 404 page

      val heroesWinSummary = getMatches(player).groupBy(_.heroId).toSeq.map { t =>
        val heroId = t._1
        val heroName = heroes.find(_.id == heroId).map(_.localizedName).getOrElse("Unknown")
        val matchResponses = t._2.map { matchResponse =>
          val playerSeq = Seq(toMatchPlayer(matchResponse, player.personaName, heroName))
          toMatchViewModel(matchResponse, playerSeq)
        }
        (heroName, matchResponses)
      }.map(toWinSummary).sortBy(-_.percentage)

      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        players.find(_.id == peer.peerPlayerId) match {
          case Some(p) => res :+ (p, peer)
          case None => res
        }
      }

      val peerPlayerWinSummary = peers.map { p =>
        WinSummary(p._1.personaName, p._2.win, p._2.games, ((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0)
      }.sortBy(-_.percentage)

      PlayerPageResponse(player, heroesWinSummary, peerPlayerWinSummary)
    }
  }

  def hero(id: Int): Future[HeroPageResponse] = {
    val playersF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      players <- playersF
      heroOption <- heroF
    } yield {
      val hero = heroOption.getOrElse(throw new Exception("Hero not found")) // TODO: should response to 404 page

      val playersWinSummary = players.flatMap(getMatches).filter(_.heroId == id).groupBy(_.player).toSeq.map { tup =>
        val matchResponses = tup._2.map { matchHead =>
          val matchPlayerList = tup._2.map { m =>
            toMatchPlayer(m, m.player.personaName, hero.localizedName)
          }
          toMatchViewModel(matchHead, matchPlayerList)
        }
        (tup._1.personaName, matchResponses)
      }.map(toWinSummary)
      HeroPageResponse(hero, playersWinSummary)
    }
  }

  private def toMatchPlayer(m: MatchResponse, playerName: String, hero: String): MatchPlayer = {
    MatchPlayer(playerName, hero, s"${m.kills}/${m.deaths}/${m.assists}")
  }

  private def toMatchViewModel(m: MatchResponse, players: Seq[MatchPlayer]): MatchViewModel = {
    val date = new Date(m.startTime.toLong * 1000L)
    val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    fmt.setTimeZone(TimeZone.getTimeZone("GMT+7")) // TODO: fucking side effect java
    val startTime = fmt.format(date.getTime)

    MatchViewModel(
      matchId = m.matchId,
      players = players,
      mode = m.getGameMode,
      startTime = startTime,
      duration = m.getDuration,
      side = m.getSide,
      result = m.getWinStatus
    )
  }

  private def toWinSummary(e: (String, Seq[MatchViewModel])): WinSummary = {
    val games = e._2.size
    val win = e._2.count(_.result == "Win")
    val percentage = (win.toDouble / games * 100).toInt / 100.0
    WinSummary(e._1, win, games, percentage)
  }

}
