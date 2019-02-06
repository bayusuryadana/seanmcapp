package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class PeerInfo(tag: Tag) extends Table[Peer](tag, "peers") {
  val playerId = column[Int]("players_id")
  val peerPlayerId = column[Int]("peer_players_id")
  val win = column[Int]("win")
  val games = column[Int]("games")

  def * = (playerId, peerPlayerId, win, games) <> (Peer.tupled, Peer.unapply)
}

object PeerRepoImpl extends TableQuery(new PeerInfo(_)) with PeerRepo with DBComponent {

  def getAll: Future[Seq[Peer]] = run(this.result)

  def insert(peers: Seq[Peer]): Future[Option[Int]] = run(this ++= peers)

  def update(peers: Seq[Peer]): Future[Seq[Int]] = {
    val query = DBIO.sequence(peers.map{ peer =>
      this.filter(e => e.playerId === peer.playerId && e.peerPlayerId === peer.peerPlayerId).update(peer)
    })
    run(query)
  }

}
