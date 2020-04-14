package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.repository.dota.{Player, PlayerRepo}
import com.seanmcapp.util.parser.decoder.{DotaMetadataDecoder, HeroAttributeResponse, HeroResponse, PlayerResponse}
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

class DotaMetadataScheduler(startTime: Int, interval: FiniteDuration, playerRepo: PlayerRepo, http: HttpRequestBuilder)
                           (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with DotaMetadataDecoder {

  private[scheduler] val dotaBaseUrl = "https://api.opendota.com/api/players/"
  private[scheduler] val dotaHeroStatsUrl = "https://api.opendota.com/api/herostats"

  override def task: Future[(Seq[PlayerResponse], Seq[HeroResponse], Seq[HeroAttributeResponse])] = {
    println("=== dota metadata fetching ===")
    val heroStatsResponse = http.sendRequest(dotaHeroStatsUrl)
    val heroResponse = decode[Seq[HeroResponse]](heroStatsResponse)
    val heroAttributeResponse = decode[Seq[HeroAttributeResponse]](heroStatsResponse)
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

      (playerResults, heroResponse, heroAttributeResponse)
    }
  }

}
