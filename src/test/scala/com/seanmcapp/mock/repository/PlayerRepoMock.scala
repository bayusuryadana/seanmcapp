package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Player, PlayerRepo}

import scala.concurrent.Future

object PlayerRepoMock extends PlayerRepo {

  private val playersList = Seq(
    Player(104466002, "Agung Putra Pasaribu", null, "hnymnky", 3122),
    Player(104787108, "Cahaya Ikhwan", null, "travengers", 2927),
    Player(105742997, "Bayu Suryadana", null, "SeanmcrayZ", 2901),
    Player(131673450, "Faris Iqbal", null, "OMEGALUL", 3492),
    Player(133805346, "Irfan Nur Afif", null, "lightzard", 2960),
  )

  override def getAll: Future[Seq[Player]] = Future.successful(playersList)

  override def get(id: Int): Future[Option[Player]] = Future.successful(playersList.find(_.id == id))
}
