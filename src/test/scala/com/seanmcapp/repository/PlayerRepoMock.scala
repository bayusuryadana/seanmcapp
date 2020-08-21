package com.seanmcapp.repository

import com.seanmcapp.repository.dota.{Player, PlayerRepo}

import scala.concurrent.Future

object PlayerRepoMock extends PlayerRepo {

  val playersList = List(
    Player(137382742, "Rahmat Rasyidi Hakim",	"https://someurl",	"kill",	Some(45)),
    Player(105742997, "Bayu Suryadana",	"https://someurl",	"SeanmcrayZ", Some(35))
  )

  override def getAll: Future[Seq[Player]] = Future.successful(playersList)

  override def update(player: Player): Future[Int] = Future.successful(1)

}
