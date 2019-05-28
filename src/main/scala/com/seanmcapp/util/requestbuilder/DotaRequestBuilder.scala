package com.seanmcapp.util.requestbuilder

import java.util.concurrent.TimeUnit

import com.seanmcapp.util.parser.{ArrayResponse, MatchResponse, PeerResponse}

import scalacache.memoization._
import scalacache.modes.sync._
import scalaj.http.Http
import scala.concurrent.duration.Duration
import spray.json._

import scalacache.Cache

trait DotaRequestBuilder {

  implicit val matchesCache: Cache[Seq[MatchResponse]]
  implicit val peersCache: Cache[Seq[PeerResponse]]

  val baseUrl = "https://api.opendota.com/api/players/"
  val duration = Duration(2, TimeUnit.HOURS)
  import com.seanmcapp.util.parser.DotaJson._

  def getMatches(id: Int): Seq[MatchResponse] = {
    memoizeSync(Some(duration)) {
      Http(baseUrl + id + "/matches").asString.body.parseJson.convertTo[ArrayResponse[MatchResponse]].res
        .map(_.appendId(id))
    }
  }

  def getMatches(ids: Seq[Int]): Seq[MatchResponse] = {
    ids.flatMap(id => getMatches(id))
  }

  def getPeers(id: Int): Seq[PeerResponse] = {
    memoizeSync(Some(duration)) {
      Http(baseUrl + id + "/peers").asString.body.parseJson.convertTo[ArrayResponse[PeerResponse]].res
    }
  }

}
