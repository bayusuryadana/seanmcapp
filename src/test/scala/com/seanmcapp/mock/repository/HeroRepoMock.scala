package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepo}

import scala.concurrent.Future

object HeroRepoMock extends HeroRepo {

  private val heroesList = Seq(
    Hero(1, "Anti-Mage", "agi", "Melee"),
    Hero(2, "Axe", "str", "Melee"),
    Hero(3, "Bane", "int", "Ranged"),
    Hero(4, "Bloodseeker", "agi", "Melee"),
    Hero(5, "Crystal Maiden", "int", "Ranged")
  )

  override def getAll: Future[Seq[Hero]] = Future.successful(heroesList)

  override def get(id: Int): Future[Option[Hero]] = Future.successful(heroesList.find(_.id == id))

}
