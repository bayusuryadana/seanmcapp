package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.repository.dota._
import com.seanmcapp.util.parser.decoder.{DotaMetadataDecoder, HeroResponse, PlayerResponse}
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

class DotaMetadataScheduler(startTime: Int, interval: FiniteDuration, playerRepo: PlayerRepo, heroRepo: HeroRepo,
                            heroAttributeRepo: HeroAttributeRepo, http: HttpRequestBuilder)
                           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with DotaMetadataDecoder {

  private[scheduler] val dotaBaseUrl = "https://api.opendota.com/api/players/"
  private[scheduler] val dotaHeroStatsUrl = "https://api.opendota.com/api/herostats"
  private[scheduler] val dotaLoreUrl = "https://raw.githubusercontent.com/bayusuryadana/dotaconstants/master/json/hero_lore.json"

  override def task: Future[(Seq[PlayerResponse], Seq[Hero], Seq[HeroAttribute])] = {
    println("=== dota metadata fetching ===")
    val heroStatsResponse = http.sendRequest(dotaHeroStatsUrl)
    val heroes = decode[Seq[HeroResponse]](heroStatsResponse)
    val heroAttributes = decode[Seq[HeroAttribute]](heroStatsResponse)

    val heroLoreResponse = http.sendRequest(dotaLoreUrl)
    val heroLoreMap = decode[Map[String, String]](heroLoreResponse)
    for {
      players <- playerRepo.getAll
    } yield {
      val playerResults = players.map { player =>
        val response = http.sendRequest(dotaBaseUrl + player.id)
        val playerResult = decode[PlayerResponse](response)
        val playerModel = Player(player.id, player.realName,
          playerResult.profile.avatarfull, playerResult.profile.personaName, playerResult.rankTier)
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
        Hero(hero.id, hero.localizedName, hero.primaryAttr, hero.attackType, hero.roles.mkString(","), heroImage, heroIcon, heroLore)
      }
      heroRepo.insertOrUpdate(heroInput)
      heroAttributeRepo.insertOrUpdate(heroAttributes)

      (playerResults, heroInput, heroAttributes)
    }
  }

}
