package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Player, PlayerRepo}

import scala.concurrent.Future

object PlayerRepoMock extends PlayerRepo {

  private val playersList = Seq(
    Player(104466002, "Agung Putra Pasaribu", null, "hnymnky", Some(55)),
    Player(104787108, "Cahaya Ikhwan", null, "travengers", None),
    Player(105742997, "Bayu Suryadana", null, "SeanmcrayZ", Some(53)),
    Player(131673450, "Faris Iqbal", null, "OMEGALUL", Some(62)),
    Player(133805346, "Irfan Nur Afif", null, "lightzard", Some(54)),
  )

  override def getAll: Future[Seq[Player]] = Future.successful(playersList)

  override def get(id: Int): Future[Option[Player]] = Future.successful(playersList.find(_.id == id))

  override def update(player: Player): Future[Int] = Future.successful(0)

}
