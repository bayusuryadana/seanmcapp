package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.repository.dota.Player
import com.seanmcapp.util.parser.{ArrayResponse, MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

import scala.io.Source
import spray.json._

trait DotaRequestBuilderMock extends DotaRequestBuilder {

  import com.seanmcapp.util.parser.DotaOutputJson._

  override def getMatches(player: Player): Seq[MatchResponse] = Source.fromResource("matches/" + player.id + ".json").mkString.parseJson
    .convertTo[ArrayResponse[MatchResponse]].res.map(_.stub(player))

  override def getPeers(id: Int): Seq[PeerResponse] = Source.fromResource("peers/" + id + ".json").mkString.parseJson
    .convertTo[ArrayResponse[PeerResponse]].res

}
