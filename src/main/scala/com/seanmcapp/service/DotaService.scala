package com.seanmcapp.service

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import com.seanmcapp.repository.dota.{HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.encoder.{HeroPageResponse, HeroWinSummary, HomePageResponse, MatchPlayer, MatchViewModel, PlayerPageResponse, PlayerWinSummary}
import com.seanmcapp.util.parser.decoder.{MatchResponseWithPlayer, PeerResponse}
import com.seanmcapp.util.requestbuilder.{DotaRequestBuilder, HttpRequestBuilder}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DotaService(playerRepo: PlayerRepo, heroRepo: HeroRepo, override val http: HttpRequestBuilder) extends DotaRequestBuilder {

  def home: Future[HomePageResponse] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val matchViewModels = players.flatMap(getMatches).groupBy(_.mr.startTime).toSeq.sortBy(-_._1).map(_._2).take(10).flatMap { identicalMatches =>
        identicalMatches.headOption.map { mrwpHead =>
          val matchPlayerList = identicalMatches.map { mrwp =>
            val hero = heroes.find(_.id == mrwp.mr.heroId).map(_.copy(lore = ""))
            MatchPlayer(mrwp.player, hero, mrwp.mr.kills, mrwp.mr.deaths, mrwp.mr.assists)
          }
          toMatchViewModel(mrwpHead, matchPlayerList)
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
      val player = players.find(_.id == id).getOrElse(throw new Exception("Player not found"))

      val heroesWinSummary = getMatches(player).groupBy(_.mr.heroId).toSeq.map { case (heroId, identicalMatches) =>
        val hero = heroes.find(_.id == heroId)
        val matchResponses = identicalMatches.map { mrwp =>
          val matchPlayer = MatchPlayer(mrwp.player, hero, mrwp.mr.kills, mrwp.mr.deaths, mrwp.mr.assists)
          toMatchViewModel(mrwp, Seq(matchPlayer))
        }
        val (win, game, percentage) = toWinSummary(matchResponses)
        HeroWinSummary(hero, win, game, percentage)
      }.sortBy(-_.percentage)

      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        players.find(_.id == peer.peerPlayerId) match {
          case Some(p) => res :+ (p, peer)
          case None => res
        }
      }

      val peerPlayerWinSummary = peers.map { p =>
        PlayerWinSummary(p._1, p._2.win, p._2.games, ((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0)
      }.sortBy(-_.percentage)

      PlayerPageResponse(player, heroesWinSummary, peerPlayerWinSummary)
    }
  }

  def hero(id: Int): Future[HeroPageResponse] = {
    val playersF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      players <- playersF
      hero <- heroF
    } yield {

      val playersWinSummary = players.flatMap(getMatches).filter(_.mr.heroId == id).groupBy(_.player).toSeq.map { tup =>
        val matchResponses = tup._2.map { matchHead =>
          val matchPlayerList = tup._2.map { mrwp =>
            MatchPlayer(mrwp.player, hero, mrwp.mr.kills, mrwp.mr.deaths, mrwp.mr.assists)
          }
          toMatchViewModel(matchHead, matchPlayerList)
        }
        val (win, game, percentage) = toWinSummary(matchResponses)
        PlayerWinSummary(tup._1, win, game, percentage)
      }
      HeroPageResponse(hero, playersWinSummary)
    }
  }

  private def toMatchViewModel(mrwp: MatchResponseWithPlayer, players: Seq[MatchPlayer]): MatchViewModel = {
    val date = new Date(mrwp.mr.startTime.toLong * 1000L)
    val fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm")
    fmt.setTimeZone(TimeZone.getTimeZone("GMT+7"))
    val startTime = fmt.format(date.getTime)

    MatchViewModel(
      matchId = mrwp.mr.matchId,
      players = players,
      mode = mrwp.mr.getGameMode,
      startTime = startTime,
      duration = mrwp.mr.getDuration,
      side = mrwp.mr.getSide,
      result = mrwp.mr.getWinStatus
    )
  }

  private def toWinSummary(matchViewList: Seq[MatchViewModel]): (Int, Int, Double) = {
    val games = matchViewList.size
    val win = matchViewList.count(_.result == "Win")
    val percentage = (win.toDouble / games * 100).toInt / 100.0
    (win, games, percentage)
  }

}
