package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepo}

import scala.concurrent.Future

object HeroRepoMock extends HeroRepo {

  private val heroesList = Seq(
    Hero(1, "Anti-Mage", "agi", "Melee", "magina story here"),
    Hero(2, "Axe", "str", "Melee", "axe story here"),
    Hero(3, "Bane", "int", "Ranged", "bane story here"),
    Hero(4, "Bloodseeker", "agi", "Melee", "strygwyr story here"),
    Hero(5, "Crystal Maiden", "int", "Ranged", "rylai story here")
  )

  override def getAll: Future[Seq[Hero]] = Future.successful(heroesList)

  override def get(id: Int): Future[Option[Hero]] = Future.successful(heroesList.find(_.id == id))

}
