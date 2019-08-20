package com.seanmcapp.util.requestbuilder

import java.util.concurrent.TimeUnit

import com.seanmcapp.repository.dota.Player
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.{ArrayResponse, MatchResponse, PeerResponse}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._
import scalaj.http.Http

import scala.concurrent.duration.Duration
import spray.json._

trait DotaRequestBuilder extends MemoryCache {
  // TODO: Waiting for DotaServiceSpec and have to wrap in an Option for http call and parsing.
  implicit val matchesCache = createCache[Seq[MatchResponse]]
  implicit val peersCache = createCache[Seq[PeerResponse]]

  val baseUrl = "https://api.opendota.com/api/players/"
  val duration = Duration(2, TimeUnit.HOURS)
  import com.seanmcapp.util.parser.DotaOutputJson._

  def getMatches(player: Player): Seq[MatchResponse] = {
    memoizeSync(Some(duration)) {
      Http(baseUrl + player.id + "/matches").asString.body.parseJson.convertTo[ArrayResponse[MatchResponse]].res
        .map(_.stub(player))
    }
  }

  def getPeers(id: Int): Seq[PeerResponse] = {
    memoizeSync(Some(duration)) {
      Http(baseUrl + id + "/peers").asString.body.parseJson.convertTo[ArrayResponse[PeerResponse]].res
    }
  }

}
