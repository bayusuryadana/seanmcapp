package com.seanmcapp.service

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import com.seanmcapp.repository.dota.{Hero, HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.encoder.{HeroPageResponse, HeroWinSummary, HomePageResponse, MatchPlayer, MatchViewModel, PlayerPageResponse, PlayerWinSummary}
import com.seanmcapp.util.parser.decoder.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.{DotaRequestBuilder, HttpRequestBuilder}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DotaService(playerRepo: PlayerRepo, heroRepo: HeroRepo, override val http: HttpRequestBuilder) extends DotaRequestBuilder {

  private[service] val MINIMUM_MATCHES = 30

  def home: Future[HomePageResponse] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val matchViewModels = players.toList.flatMap(getMatches).groupBy(_.startTime).toSeq.sortBy(-_._1).map(_._2).take(10).flatMap { identicalMatches =>
        identicalMatches.headOption.map { matchResponseHead =>
          val matchPlayerList = identicalMatches.map { matchResponse =>
            val hero = heroes.find(_.id == matchResponse.heroId).getOrElse(createHero(matchResponse.heroId)).copy(lore = "")
            val player = matchResponse.player.get // it won't get an exception here
            MatchPlayer(player, hero, matchResponse.kills, matchResponse.deaths, matchResponse.assists)
          }
          toMatchViewModel(matchResponseHead, matchPlayerList)
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
      val matches = getMatches(player)
      val recentMatches = matches.sortBy(-_.startTime).take(10).map(m => toMatchViewModel(m, List.empty))
      val (totalWin, totalGames, totalPercentage) = toWinSummary(matches.map(m => toMatchViewModel(m, List.empty)))
      val playerWinSummary = PlayerWinSummary(player, totalWin, totalGames-totalWin, totalPercentage, 0)

      val heroesTmpSummary = matches.groupBy(_.heroId).toSeq.map { case (heroId, identicalMatches) =>
        val hero = heroes.find(_.id == heroId).getOrElse(createHero(heroId))
        val matchResponses = identicalMatches.map { matchResponse =>
          val player = matchResponse.player.get // it won't get an exception here
          val matchPlayer = MatchPlayer(player, hero, matchResponse.kills, matchResponse.deaths, matchResponse.assists)
          toMatchViewModel(matchResponse, List(matchPlayer))
        }
        val (win, game, percentage) = toWinSummary(matchResponses)
        (hero, win, game, percentage)
      }
      val cHero = heroesTmpSummary.map(_._4).sum / heroesTmpSummary.map(_._3).sum
      val heroesWinSummary = heroesTmpSummary.map { case (hero, win, game, percentage) =>
        HeroWinSummary(hero.copy(lore = ""), win, game, percentage, calculateRating(game, percentage, cHero))
      }.filter(_.games >= MINIMUM_MATCHES).sortBy(-_.rating)

      val peers = getPeers(id).foldLeft(List.empty[(Player, PeerResponse)]) { (res, peer) =>
        players.find(_.id == peer.peerPlayerId) match {
          case Some(p) => res :+ (p, peer)
          case None => res
        }
      }

      val peerPlayerTmpSummary = peers.map { p =>
        (p._1, p._2.win, p._2.games, ((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0)
      }
      val cPeer = peerPlayerTmpSummary.map(_._4).sum / peerPlayerTmpSummary.map(_._3).sum
      val peerPlayerWinSummary = peerPlayerTmpSummary.map { case (p, win, game, percentage) =>
        PlayerWinSummary(p, win, game, percentage, calculateRating(game, percentage, cPeer))
      }.filter(_.games >= MINIMUM_MATCHES).sortBy(-_.rating)

      PlayerPageResponse(player, heroesWinSummary, peerPlayerWinSummary, recentMatches, playerWinSummary)
    }
  }

  def hero(id: Int): Future[HeroPageResponse] = {
    val playersF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      players <- playersF
      hero <- heroF
    } yield {
      val playersTmpSummary = players.flatMap(getMatches).filter(_.heroId == id).groupBy(_.player).toSeq.map { tup =>
        val matchResponses = tup._2.map { matchHead =>
          val player = matchHead.player.get // it won't get an exception here
          val matchPlayerList = tup._2.map { mrwp =>
            MatchPlayer(player, hero.getOrElse(createHero(id)), mrwp.kills, mrwp.deaths, mrwp.assists)
          }.toList
          toMatchViewModel(matchHead, matchPlayerList)
        }
        val (win, game, percentage) = toWinSummary(matchResponses)
        (tup._1, win, game, percentage)
      }
      val cPlayer = playersTmpSummary.map(_._4).sum / playersTmpSummary.map(_._3).sum
      val playersWinSummary = playersTmpSummary.map { case (p, win, game, percentage) =>
        PlayerWinSummary(p.get, win, game, percentage, calculateRating(game, percentage, cPlayer))
      }.filter(_.games >= MINIMUM_MATCHES).sortBy(-_.rating)
      HeroPageResponse(hero, playersWinSummary)
    }
  }

  private def toMatchViewModel(mr: MatchResponse, players: List[MatchPlayer]): MatchViewModel = {
    val date = new Date(mr.startTime.toLong * 1000L)
    val fmt = new SimpleDateFormat("dd-MM-yyyy HH:mm")
    fmt.setTimeZone(TimeZone.getTimeZone("GMT+7"))
    val startTime = fmt.format(date.getTime)

    MatchViewModel(
      matchId = mr.matchId,
      players = players,
      mode = mr.getGameMode,
      startTime = startTime,
      duration = mr.getDuration,
      side = mr.getSide,
      result = mr.getWinStatus
    )
  }

  private def toWinSummary(matchViewList: Seq[MatchViewModel]): (Int, Int, Double) = {
    val games = matchViewList.size
    val win = matchViewList.count(_.result == "Win")
    val percentage = (win.toDouble / games * 100).toInt / 100.0
    (win, games, percentage)
  }

  private def calculateRating(v: Int, R: Double, C: Double): Double = {
    // using this formula from here https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating
    val m = MINIMUM_MATCHES
    (R * v + C * m) / (v + m)
  }

  private def createHero(id: Int) = Hero(id, "Unknown", "???", "", "", "", "", "")

}
