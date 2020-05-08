package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Player, PlayerRepo}

import scala.concurrent.Future

object PlayerRepoMock extends PlayerRepo {

  val playersList = List(
    Player(104466002, "Agung Putra Pasaribu", "https://someurl", "hnymnky", Some(55)),
    Player(131673450, "Faris Iqbal", "https://someurl", "OMEGALUL", Some(62)),
  )

  override def getAll: Future[Seq[Player]] = Future.successful(playersList)

  override def get(id: Int): Future[Option[Player]] = Future.successful(playersList.find(_.id == id))

  override def update(player: Player): Future[Int] = Future.successful(0)

}
