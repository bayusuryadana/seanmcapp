package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.repository.dota.Player
import com.seanmcapp.util.parser.decoder.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

import scala.io.Source

trait DotaRequestBuilderMock extends DotaRequestBuilder {

  override def getMatches(player: Player): Seq[MatchResponse] = {
    val source = Source.fromResource("matches/" + player.id + ".json").mkString
    decode[Seq[MatchResponse]](source)
  }

  override def getPeers(id: Int): Seq[PeerResponse] = {
    val source = Source.fromResource("peers/" + id + ".json").mkString
    decode[Seq[PeerResponse]](source)
  }

}
