package com.seanmcapp.service

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import com.seanmcapp.repository.dota._
import com.seanmcapp.util.parser.encoder._
import com.seanmcapp.util.parser.decoder.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.{DotaRequestBuilder, HttpRequestBuilder}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DotaService(playerRepo: PlayerRepo, heroRepo: HeroRepo, heroAttrRepo: HeroAttributeRepo, override val http: HttpRequestBuilder) extends DotaRequestBuilder {

  private[service] val MINIMUM_MATCHES = 30

  def dashboard: Future[DashboardPageResponse] = {
    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val heroesMap = heroes.map(hero => hero.id -> hero).toMap

      val playersInfo = players.map { player =>
        val playerMatches = getMatches(player)
        val winSummary = toWinSummary(playerMatches)
        val recentMatches = playerMatches.sortBy(-_.startTime.toLong).take(3).map(_.formatStartTime)
        val heroesWinSummary = playerMatches.groupBy(_.heroId).toSeq.map { case (heroId, matches) =>
          val hero = heroesMap.getOrElse(heroId, dummyHero(heroId))
          hero -> toWinSummary(matches)
        }
        val cHero = heroesWinSummary.map(_._2.percentage).sum / heroesWinSummary.map(_._2.games).sum
        val topHero = heroesWinSummary.map { case (hero, heroWinSummary) =>
          hero -> heroWinSummary.copy(rating = Some(calculateRating(heroWinSummary.win, heroWinSummary.games, cHero)))
        }.sortBy(-_._2.rating.getOrElse(0.0)).take(3)
        PlayerInfo(player, winSummary, recentMatches, topHero)
      }

      val heroesInfo = heroes.map { hero =>
        val playersWinSummary = players.map { player =>
          val playerMatches = getMatches(player).filter(_.heroId == hero.id)
          player -> toWinSummary(playerMatches)
        }
        val cPlayer = playersWinSummary.map(_._2.percentage).sum / playersWinSummary.map(_._2.games).sum
        val topPlayer = playersWinSummary.map { case (player, playerWinSummary) =>
          player -> playerWinSummary.copy(rating = Some(calculateRating(playerWinSummary.win, playerWinSummary.games, cPlayer)))
        }.sortBy(-_._2.rating.getOrElse(0.0)).take(3)
        HeroInfo(hero, topPlayer)
      }

      DashboardPageResponse(playersInfo, heroesInfo)
    }
  }

  private def toWinSummary(matchViewList: Seq[MatchResponse]): WinSummary = {
    val games = matchViewList.size
    val win = matchViewList.count(_.getWinStatus == "Win")
    val percentage = (win.toDouble / games * 100).toInt / 100.0
    WinSummary(win, games, percentage, None)
  }

  private def calculateRating(v: Int, R: Double, C: Double): Double = {
    /**
      * using this formula from here https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating
      * R = mean of the item (win)
      * v = number of total item (games)
      * m = minimum games required to be listed in
      * C = mean of every sum item's percentage (c)
      *
      */

    val m = MINIMUM_MATCHES
    (R * v + C * m) / (v + m)
  }

  private def dummyHero(id: Int) = Hero(id, "Unknown", "???", "", "", "", "", "")

}
