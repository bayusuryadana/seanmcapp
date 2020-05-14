package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepo}

import scala.concurrent.Future

object HeroRepoMock extends HeroRepo {

  private val heroesList = List(
    Hero(18, "Sven", "str", "Melee", "Carry,Disabler,Initiator,Durable,Nuker", "sven_full.png", "sven_icon.png", "sven story here"),
    Hero(26, "Lion", "int", "Ranged", "Support,Disabler,Nuker,Initiator", "lion_full.png", "lion_icon.png", "lion story here"),
  )

  override def getAll: Future[Seq[Hero]] = Future.successful(heroesList)

  override def insertOrUpdate(heroes: Seq[Hero]): Seq[Future[Int]] = heroes.map(_ => Future.successful(1))

}
