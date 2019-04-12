package com.seanmcapp.service

import com.seanmcapp.model.{HeroViewModel, MatchViewModel, PeerViewModel, PlayerViewModel}
import com.seanmcapp.repository.dota.{HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequest
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DotaService extends DotaRequest {

  val playerRepo: PlayerRepo
  val heroRepo: HeroRepo

  def getRecentMatches: Future[Seq[MatchViewModel]] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val matches = getMatches(players.map(_.id)).sortBy(m => -m._2.startTime).take(20)
      val heroesMap = heroes.map(h => (h.id, h)).toMap

      matches.map { matchTuple =>
        val m = matchTuple._2
        val playerName = players.find(_.id == matchTuple._1).map(_.personaName).getOrElse("Unknown Player")
        val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
        toMatchViewModel(m, playerName, hero)
      }
    }
  }

  def getPlayers: Future[Seq[PlayerViewModel]] = playerRepo.getAll.map(_.map { p =>
    PlayerViewModel(p.realName, p.personaName, p.MMREstimate)
  })

  def getPlayerMatches(id: Int): Future[(Seq[MatchViewModel], Seq[PeerViewModel])] = {
    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll
    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val playerViewModel = players.find(_.id == id)
        .map(p => PlayerViewModel(p.realName, p.personaName, p.MMREstimate))
        .getOrElse(throw new Exception("Player not found"))
      val matches = getMatches(id)
      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        val player = players.find(_.id == peer.peerPlayerId)
        if (player.isDefined) res :+ (player.get, peer) else res
      }
      val heroesMap = heroes.map(h => (h.id, h)).toMap

      val matchViewModel = matches.map { m =>
        val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
        toMatchViewModel(m, playerViewModel.personaName, hero)
      }

      val peerViewModel = peers.map { p =>
        PeerViewModel(p._1.personaName, p._2.win, ((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0)
      }

      (matchViewModel, peerViewModel)
    }
  }

  def getHeroes: Future[Seq[HeroViewModel]] = heroRepo.getAll.map(_.map { h =>
    HeroViewModel(h.id, h.localizedName, h.primaryAttr, h.attackType)
  })

  def getHeroMatches(id: Int): Future[Seq[(String, Seq[MatchViewModel])]] = {
    val playersF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      players <- playersF
      hero <- heroF
    } yield {
      val heroName = hero.map(_.localizedName).getOrElse(throw new Exception("Hero not found"))

      getMatches(players.map(_.id)).map {
        case matchTuple if matchTuple._2.heroId == id =>
          val m = matchTuple._2
          val playerName = players.find(_.id == matchTuple._1).map(_.personaName).getOrElse("Unknown Player")
          toMatchViewModel(m, playerName, heroName)
      }.groupBy(_.name).toSeq // TODO: get win percentage of each player
    }
  }

  private def toMatchViewModel(m: MatchResponse, playerName: String, hero: String): MatchViewModel = {
    MatchViewModel(
      name = playerName,
      matchId = m.matchId,
      side = m.getSide,
      result = m.getWinStatus,
      duration = m.getDuration,
      mode = m.getGameMode,
      hero = hero,
      startTime = new DateTime(m.startTime.toLong * 1000),
      kda = s"${m.kills}/${m.deaths}/${m.assists}"
    )
  }

}
