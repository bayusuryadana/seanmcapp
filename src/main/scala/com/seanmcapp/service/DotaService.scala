package com.seanmcapp.service

import com.seanmcapp.repository.dota.{Hero, HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class MatchViewModel(matchId: Long, name: String, hero: String, kda: String, mode: String, startTime: Long,
                          duration: String, side: String, result: String)

case class WinSummary(peerName: String, win: Int, games: Int, percentage:Double)

case class HomePageResponse(matches: Seq[MatchViewModel], players: Seq[Player], heroes: Seq[Hero])

case class PlayerPageResponse(player: Player, heroes: Seq[WinSummary], peers: Seq[WinSummary])

case class HeroPageResponse(hero: Hero, players: Seq[WinSummary])

trait DotaService extends DotaRequestBuilder {

  val playerRepo: PlayerRepo
  val heroRepo: HeroRepo

  protected val heroImageBaseURL = "https://api.opendota.com/apps/dota2/images/heroes/"

  def home: Future[HomePageResponse] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val heroesMap = heroes.map(h => (h.id, h)).toMap
      val matchViewModels = getMatches(players.map(_.id)).sortBy(m => -m._2.startTime).take(10).map { matchTuple =>
        val m = matchTuple._2
        val playerName = players.find(_.id == matchTuple._1).map(_.personaName).getOrElse("Unknown Player")
        val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
        toMatchViewModel(m, playerName, hero)
      }

      HomePageResponse(matchViewModels, players, heroes.map(hero => hero.copy(lore = "", image = heroImageBaseURL + hero.image)))
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
        toMatchViewModel(m, player.personaName, hero)
      }.groupBy(_.hero).map(toWinSummary).toSeq

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
        case matchTuple if matchTuple._2.heroId == id =>
          val m = matchTuple._2
          val playerName = players.find(_.id == matchTuple._1).map(_.personaName).getOrElse("Unknown Player")
          toMatchViewModel(m, playerName, hero.localizedName)
      }.groupBy(_.name).map(toWinSummary).toSeq

      HeroPageResponse(hero.copy(image = heroImageBaseURL + hero.image), playersWinSummary)
    }
  }

  private def toMatchViewModel(m: MatchResponse, playerName: String, hero: String): MatchViewModel = {
    MatchViewModel(
      matchId = m.matchId,
      name = playerName,
      hero = hero,
      kda = s"${m.kills}/${m.deaths}/${m.assists}",
      mode = m.getGameMode,
      startTime = m.startTime.toLong,
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
