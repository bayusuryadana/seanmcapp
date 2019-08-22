package com.seanmcapp.util.requestbuilder

import java.util.concurrent.TimeUnit

import com.seanmcapp.repository.dota.Player
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.decoder.{DotaInputDecoder, MatchResponse, MatchResponseWithPlayer, PeerResponse}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import scala.concurrent.duration.Duration

trait DotaRequestBuilder extends DotaInputDecoder with MemoryCache {

  val http: HttpRequestBuilder

  implicit val matchesCache = createCache[Seq[MatchResponseWithPlayer]]
  implicit val peersCache = createCache[Seq[PeerResponse]]

  val baseUrl = "https://api.opendota.com/api/players/"
  val duration = Duration(2, TimeUnit.HOURS)

  def getMatches(player: Player): Seq[MatchResponseWithPlayer] = {
    memoizeSync(Some(duration)) {
      val response = http.sendRequest(baseUrl + player.id + "/matches")
      decode[Seq[MatchResponse]](response).map { m =>
        MatchResponseWithPlayer(player, m)
      }
    }
  }

  def getPeers(id: Int): Seq[PeerResponse] = {
    memoizeSync(Some(duration)) {
      val response = http.sendRequest(baseUrl + id + "/peers")
      decode[Seq[PeerResponse]](response)
    }
  }

}
