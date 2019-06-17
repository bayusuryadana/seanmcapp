package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.util.parser.{ArrayResponse, MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

import scala.io.Source
import spray.json._

trait DotaRequestBuilderMock extends DotaRequestBuilder {

  import com.seanmcapp.util.parser.DotaOutputJson._

  override def getMatches(id: Int): Seq[MatchResponse] = Source.fromResource("matches/" + id + ".json").mkString.parseJson
    .convertTo[ArrayResponse[MatchResponse]].res.map(_.appendId(id))

  override def getPeers(id: Int): Seq[PeerResponse] = Source.fromResource("peers/" + id + ".json").mkString.parseJson
    .convertTo[ArrayResponse[PeerResponse]].res

}
