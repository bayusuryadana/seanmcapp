package com.seanmcapp.repository.dota

import scala.concurrent.Future

case class Peer(playerId: Int, peerPlayerId: Int, win: Int, games: Int)

trait PeerRepo {

  def getAll: Future[Seq[Peer]]

  def insert(peers: Seq[Peer]): Future[Option[Int]]

  def update(peers: Seq[Peer]): Future[Seq[Int]]

}
