package com.seanmcapp.fetcher

import com.seanmcapp.repository.dota.{MatchRepo, PeerRepo, PlayerRepo}
import com.seanmcapp.util.parser.{ArrayResponse, MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequest
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

trait DotaFetcher extends DotaRequest {

  val playerRepo: PlayerRepo
  val matchRepo: MatchRepo
  val peerRepo: PeerRepo

  def fetch: Future[Unit] = {
    import com.seanmcapp.util.parser.DotaJson._

    val playersF = playerRepo.getAll
    val matchesF = matchRepo.getAllMatchId
    val peersF = peerRepo.getAll

    for {
      players <- playersF
      matches <- matchesF
      peers <- peersF
    } yield {
      players.map { id =>
        println(id)

        val matchesResult = getMatches(id).body.parseJson.convertTo[ArrayResponse[MatchResponse]]
          .res.map(_.toMatch(id)).filterNot(e => matches.contains(e.id))
        Await.result(matchRepo.insert(matchesResult), Duration.Inf)

        val peersResult = getPeers(id).body.parseJson.convertTo[ArrayResponse[PeerResponse]]
          .res.map(_.toPeer(id)).filter(e => players.contains(e.peerPlayerId))
        val existingPeerPlayer = peers.filter(_.playerId == id).map(_.peerPlayerId).toSet
        val peerSplit = peersResult.partition(p => existingPeerPlayer.contains(p.peerPlayerId))
        Await.result(peerRepo.update(peerSplit._1), Duration.Inf)
        Await.result(peerRepo.insert(peerSplit._2), Duration.Inf)
      }
    }
  }

}
