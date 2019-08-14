package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.repository.dota.{Player, PlayerRepoImpl}
import com.seanmcapp.util.parser.PlayerResponse
import scalaj.http.Http
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.FiniteDuration

class DotaMetadataFetcherScheduler(startTime: Int, interval: FiniteDuration)
                                  (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val playerRepo = PlayerRepoImpl
  private val dotaBaseUrl = "https://api.opendota.com/api/players/"

  override def task: Future[Seq[PlayerResponse]] = {
    println("=== dota metadata fetching ===")
    import com.seanmcapp.util.parser.DotaInputJson._
    for {
      players <- playerRepo.getAll
    } yield {
      players.map { player =>
        val playerResult = Http(dotaBaseUrl + player.id).asString.body.parseJson.convertTo[PlayerResponse]
        val playerModel = Player(player.id, player.realName,
          playerResult.profile.avatarfull, playerResult.profile.personaName, playerResult.rankTier)
        playerRepo.update(playerModel)
        playerResult
      }
    }
  }

}
