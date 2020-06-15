package com.seanmcapp.util.requestbuilder

import java.util.concurrent.TimeUnit

import com.seanmcapp.repository.dota.Player
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.decoder.{DotaInputDecoder, MatchResponse, PeerResponse}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import scala.concurrent.duration.Duration

trait DotaRequestBuilder extends DotaInputDecoder with MemoryCache {

  val http: HttpRequestBuilder

  implicit val matchesCache = createCache[Seq[MatchResponse]]
  implicit val peersCache = createCache[Seq[PeerResponse]]

  val baseUrl = "https://api.opendota.com/api/players/"
  val duration = Duration(2, TimeUnit.HOURS)

  def getMatches(player: Player): Seq[MatchResponse] = {
    memoizeSync(Some(duration)) {
      val response = http.sendGetRequest(baseUrl + player.id + "/matches")
      decode[Seq[MatchResponse]](response)
    }
  }

  def getPeers(id: Int): Seq[PeerResponse] = {
    memoizeSync(Some(duration)) {
      val response = http.sendGetRequest(baseUrl + id + "/peers")
      decode[Seq[PeerResponse]](response)
    }
  }

}
