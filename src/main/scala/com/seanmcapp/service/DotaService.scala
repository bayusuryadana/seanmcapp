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
      val heroesMap = heroes.map(h => (h.id, h)).toMap
      val matchViewModels = getMatches(players.map(_.id)).sortBy(m => -m.startTime).take(50).groupBy(_.matchId).map { matchTuple =>
        val matchPlayerList = matchTuple._2.map { m =>
          val playerName = players.find(_.id == m.playerId.get).map(_.personaName).getOrElse("Unknown Player")
          val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
          toMatchPlayer(m, playerName, hero)
        }
        toMatchViewModel(matchTuple._2.head, matchPlayerList)
      }.take(10).toSeq

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
      val player = players.find(_.id == id).getOrElse(throw new Exception("Player not found"))

      val heroesMap = heroes.map(h => (h.id, h)).toMap
      val heroesWinSummary = getMatches(id).map { m =>
        val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
        val playerSeq = Seq(toMatchPlayer(m, player.personaName, hero))
        toMatchViewModel(m, playerSeq)
      }.groupBy(_.players.head.hero).map(toWinSummary).toSeq

      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        val player = players.find(_.id == peer.peerPlayerId)
        if (player.isDefined) res :+ (player.get, peer) else res
      }

      val peerPlayerWinSummary = peers.map { p =>
        WinSummary(p._1.personaName, p._2.win, p._2.games, ((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0)
      }

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
      val hero = heroOption.getOrElse(throw new Exception("Hero not found"))
      val playersWinSummary = getMatches(players.map(_.id)).collect {
        case m if m.heroId == id =>
          val playerName = players.find(_.id == m.playerId.get).map(_.personaName).getOrElse("Unknown Player")
          val playerSeq = Seq(toMatchPlayer(m, playerName, hero.localizedName))
          toMatchViewModel(m, playerSeq)
      }.groupBy(_.players.head.hero).map(toWinSummary).toSeq

      HeroPageResponse(hero, playersWinSummary)
    }
  }

  private def toMatchPlayer(m: MatchResponse, playerName: String, hero: String): MatchPlayer = {
    MatchPlayer(playerName, hero, s"${m.kills}/${m.deaths}/${m.assists}")
  }

  private def toMatchViewModel(m: MatchResponse, players: Seq[MatchPlayer]): MatchViewModel = {
    val date = new Date(m.startTime.toLong * 1000L)
    val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    // fucking side effect java
    fmt.setTimeZone(TimeZone.getTimeZone("GMT+7"))
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
