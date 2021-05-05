package com.seanmcapp.service

import com.seanmcapp.external.{DotaClient, MatchResponse, PlayerResponse}
import com.seanmcapp.repository.dota._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class WinSummary(win: Int, games: Int, percentage: Double, rating: Option[Double])

case class PlayerInfo(player:Player, winSummary: WinSummary, matches: Seq[MatchResponse], topHero: Seq[(Hero, WinSummary)])

case class HeroInfo(hero: Hero, heroAttribute: HeroAttribute, topPlayer: Seq[(Player, WinSummary)])

case class HomePageResponse(players: Seq[PlayerInfo], heroes: Seq[HeroInfo])

class DotaService(playerRepo: PlayerRepo, heroRepo: HeroRepo, heroAttrRepo: HeroAttributeRepo,
                  dotaClient: DotaClient) extends ScheduledTask {

  private[service] val MINIMUM_MATCHES = 30
  private val heroImageBaseURL = "https://api.opendota.com/apps/dota2/images/heroes/"
  private val rankImageBaseURL = "https://www.opendota.com/assets/images/dota2/rank_icons/"

  // $COVERAGE-OFF$
  def home: Future[String] = {
    val homePageResponseF = getHomePageData
    homePageResponseF.map { homePageResponse =>
      com.seanmcapp.dota.html.home(heroImageBaseURL, rankImageBaseURL, homePageResponse).body
    }
  }
  // $COVERAGE-ON$

  private[service] def getHomePageData: Future[HomePageResponse] = {
    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll
    val heroAttributesF = heroAttrRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
      heroAttributes <- heroAttributesF
    } yield {
      val heroesMap = heroes.map(hero => hero.id -> hero).toMap
      val heroAttributesMap = heroAttributes.map(attr => attr.id -> attr).toMap

      val playersInfo = players.map { player =>
        val playerMatches = dotaClient.getMatches(player)
        val winSummary = toWinSummary(playerMatches)
        val recentMatches = playerMatches.sortBy(-_.start_time).take(3)
        val heroesWinSummary = playerMatches.groupBy(_.hero_id).toSeq.map { case (heroId, matches) =>
          val hero = heroesMap.getOrElse(heroId, Hero.dummy(heroId))
          hero -> toWinSummary(matches)
        }
        val cHero = heroesWinSummary.map(_._2.percentage).sum / heroesWinSummary.map(_._2.games).sum
        val topHero = heroesWinSummary.map { case (hero, heroWinSummary) =>
          hero.copy(lore = "") -> heroWinSummary.copy(rating = Some(calculateRating(heroWinSummary.percentage, heroWinSummary.games, cHero)))
        }.sortBy(-_._2.rating.getOrElse(0.0)).take(3)
        PlayerInfo(player, winSummary, recentMatches, topHero)
      }.sortBy(-_.matches.head.start_time)

      val heroesInfo = heroes.map { hero =>
        val playersWinSummary = players.map { player =>
          val playerMatches = dotaClient.getMatches(player).filter(_.hero_id == hero.id)
          player -> toWinSummary(playerMatches)
        }
        val cPlayer = playersWinSummary.map(_._2.percentage).sum / playersWinSummary.map(_._2.games).sum
        val topPlayer = playersWinSummary.map { case (player, playerWinSummary) =>
          player -> playerWinSummary.copy(rating = Some(calculateRating(playerWinSummary.percentage, playerWinSummary.games, cPlayer)))
        }.sortBy(-_._2.rating.getOrElse(0.0)).take(3)
        HeroInfo(hero, heroAttributesMap.getOrElse(hero.id, HeroAttribute.dummy(hero.id)), topPlayer)
      }.sortBy(_.hero.id)

      HomePageResponse(playersInfo, heroesInfo)
    }
  }

  private def toWinSummary(matchViewList: Seq[MatchResponse]): WinSummary = {
    val games = matchViewList.size
    val win = matchViewList.count(_.getWinStatus == "Win")
    val percentage = (win.toDouble / games * 100).toInt / 100.0
    WinSummary(win, games, percentage, None)
  }

  /**
    * using this formula from here https://stackoverflow.com/questions/1411199/what-is-a-better-way-to-sort-by-a-5-star-rating
    * R = mean of the item (win)
    * v = number of total item (games)
    * m = minimum games required to be listed in
    * C = mean of every sum item's percentage (c)
    *
    */
  private def calculateRating(R: Double, v:Int, C: Double): Double = {
    val m = MINIMUM_MATCHES
    (R * v + C * m) / (v + m)
  }

  override def run: Future[(Seq[PlayerResponse], Seq[Hero], Seq[HeroAttribute])] = {
    for {
      players <- playerRepo.getAll
    } yield {
      val (heroes, heroAttributes) = dotaClient.getHeroStatsAndAttr
      val heroLoreMap = dotaClient.getHeroLore
      val playerResults = players.map { player =>
        val playerResult = dotaClient.getPlayerDetail(player)
        val playerModel = Player(player.id, player.realName,
          playerResult.profile.avatarfull, playerResult.profile.personaname, playerResult.rank_tier)
        playerRepo.update(playerModel)
        playerResult
      }

      val heroInput = heroes.map { hero =>
        val heroImage = hero.img.stripPrefix("/apps/dota2/images/heroes/").stripSuffix("?")
        val heroIcon = hero.icon.stripPrefix("/apps/dota2/images/heroes/")
        val heroName = heroImage.stripSuffix("_full.png")
        val heroLore = heroLoreMap.get(heroName) match {
          case Some(h) => h
          case None =>
            println(s"lore not found for $heroName")
            ""
        }
        Hero(hero.id, hero.localized_name, hero.primary_attr, hero.attack_type, hero.roles.mkString(","), heroImage, heroIcon, heroLore)
      }
      heroRepo.insertOrUpdate(heroInput)
      heroAttrRepo.insertOrUpdate(heroAttributes)

      (playerResults, heroInput, heroAttributes)
    }
  }

}
