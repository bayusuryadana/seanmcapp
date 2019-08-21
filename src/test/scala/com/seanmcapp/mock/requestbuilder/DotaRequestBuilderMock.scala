package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.repository.dota.Player
import com.seanmcapp.util.parser.{MatchResponse, MatchResponseWithPlayer, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

import scala.io.Source

trait DotaRequestBuilderMock extends DotaRequestBuilder {

  override def getMatches(player: Player): Seq[MatchResponseWithPlayer] = {
    val source = Source.fromResource("matches/" + player.id + ".json").mkString
    decode[Seq[MatchResponse]](source).map { matchResponse =>
      MatchResponseWithPlayer(player, matchResponse)
    }
  }

  override def getPeers(id: Int): Seq[PeerResponse] = {
    val source = Source.fromResource("peers/" + id + ".json").mkString
    decode[Seq[PeerResponse]](source)
  }

}
