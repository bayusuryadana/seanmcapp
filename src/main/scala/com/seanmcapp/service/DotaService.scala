package com.seanmcapp.service

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import com.seanmcapp.repository.dota.{HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.encoder.{HeroPageResponse, HomePageResponse, MatchPlayer, MatchViewModel, PlayerPageResponse, WinSummary}
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
            val heroName = heroes.find(_.id == mrwp.mr.heroId).map(_.localizedName).getOrElse("Unknown")
            MatchPlayer(mrwp.player.personaName, heroName, s"${mrwp.mr.kills}/${mrwp.mr.deaths}/${mrwp.mr.assists}")
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
      val player = players.find(_.id == id).getOrElse(throw new Exception("Player not found")) // TODO: should response to 404 page

      val heroesWinSummary = getMatches(player).groupBy(_.mr.heroId).toSeq.map { case (heroId, identicalMatches) =>
        val heroName = heroes.find(_.id == heroId).map(_.localizedName).getOrElse("Unknown")
        val matchResponses = identicalMatches.map { mrwp =>
          val matchPlayer = MatchPlayer(mrwp.player.personaName, heroName, s"${mrwp.mr.kills}/${mrwp.mr.deaths}/${mrwp.mr.assists}")
          toMatchViewModel(mrwp, Seq(matchPlayer))
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

      val playersWinSummary = players.flatMap(getMatches).filter(_.mr.heroId == id).groupBy(_.player).toSeq.map { tup =>
        val matchResponses = tup._2.map { matchHead =>
          val matchPlayerList = tup._2.map { m =>
            MatchPlayer(m.player.personaName, hero.localizedName, s"${m.mr.kills}/${m.mr.deaths}/${m.mr.assists}")
          }
          toMatchViewModel(matchHead, matchPlayerList)
        }
        (tup._1.personaName, matchResponses)
      }.map(toWinSummary)
      HeroPageResponse(hero, playersWinSummary)
    }
  }

  private def toMatchViewModel(mrwp: MatchResponseWithPlayer, players: Seq[MatchPlayer]): MatchViewModel = {
    val date = new Date(mrwp.mr.startTime.toLong * 1000L)
    val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm")
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

  private def toWinSummary(e: (String, Seq[MatchViewModel])): WinSummary = {
    val games = e._2.size
    val win = e._2.count(_.result == "Win")
    val percentage = (win.toDouble / games * 100).toInt / 100.0
    WinSummary(e._1, win, games, percentage)
  }

}
