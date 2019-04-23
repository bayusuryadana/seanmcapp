package com.seanmcapp.mock.requestbuilder

import com.seanmcapp.util.parser.{MatchResponse, PeerResponse}
import com.seanmcapp.util.requestbuilder.DotaRequestBuilder

// TODO: implement mock request builder
trait DotaRequestMock extends DotaRequestBuilder {

  override def getMatches(id: Int): Seq[MatchResponse] = ???

  override def getMatches(ids: Seq[Int]): Seq[(Int, MatchResponse)] = ???

  override def getPeers(id: Int): Seq[PeerResponse] = ???

}
