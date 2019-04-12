package com.seanmcapp.service

import akka.http.scaladsl.model.HttpResponse
import com.seanmcapp.util.viewbuilder.{HeroViewModel, MatchViewModel, PeerViewModel, PlayerViewModel}
import com.seanmcapp.repository.dota.{HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder
import com.seanmcapp.util.viewbuilder.DotaViewBuilder
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DotaService extends DotaRequestBuilder with DotaViewBuilder {

  val playerRepo: PlayerRepo
  val heroRepo: HeroRepo

  def home: Future[HttpResponse] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val heroesMap = heroes.map(h => (h.id, h)).toMap
      val matchViewModels = getMatches(players.map(_.id)).sortBy(m => -m._2.startTime).take(20).map { matchTuple =>
        val m = matchTuple._2
        val playerName = players.find(_.id == matchTuple._1).map(_.personaName).getOrElse("Unknown Player")
        val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
        toMatchViewModel(m, playerName, hero)
      }

      val playerViewModels = players.map { p =>
        PlayerViewModel(p.id, p.realName, p.personaName, p.avatarFull)
      }

      val heroViewModel = heroes.map { h =>
        HeroViewModel(h.id, h.localizedName, h.primaryAttr, h.image, h.lore.getOrElse(""))
      }

      HttpResponse(entity = buildHomeView(matchViewModels, playerViewModels, heroViewModel))
    }
  }

  def player(id: Int): Future[HttpResponse] = {
    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll
    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val player = players.find(_.id == id).getOrElse(throw new Exception("Player not found"))

      val matches = getMatches(id)
      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        val player = players.find(_.id == peer.peerPlayerId)
        if (player.isDefined) res :+ (player.get, peer) else res
      }
      val heroesMap = heroes.map(h => (h.id, h)).toMap

      val matchViewModel = matches.map { m =>
        val hero = heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")
        toMatchViewModel(m, player.personaName, hero)
      }

      val peerViewModel = peers.map { p =>
        PeerViewModel(p._1.personaName, p._2.win, ((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0)
      }

      HttpResponse(entity = buildPlayerView(player, matchViewModel, peerViewModel))
    }
  }

  def hero(id: Int): Future[HttpResponse] = {
    val playersF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      players <- playersF
      heroOption <- heroF
    } yield {
      val hero = heroOption.getOrElse(throw new Exception("Hero not found"))
      val matches = getMatches(players.map(_.id)).map {
        case matchTuple if matchTuple._2.heroId == id =>
          val m = matchTuple._2
          val playerName = players.find(_.id == matchTuple._1).map(_.personaName).getOrElse("Unknown Player")
          toMatchViewModel(m, playerName, hero.localizedName)
      }//.groupBy(_.name).toSeq // TODO: get win percentage of each player

      HttpResponse(entity = buildHeroView(hero, matches))
    }
  }

  private def toMatchViewModel(m: MatchResponse, playerName: String, hero: String): MatchViewModel = {
    MatchViewModel(
      matchId = m.matchId,
      name = playerName,
      hero = hero,
      kda = s"${m.kills}/${m.deaths}/${m.assists}",
      mode = m.getGameMode,
      startTime = new DateTime(m.startTime.toLong * 1000),
      duration = m.getDuration,
      side = m.getSide,
      result = m.getWinStatus
    )
  }

}
