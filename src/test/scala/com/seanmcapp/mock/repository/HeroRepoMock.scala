package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepo}

import scala.concurrent.Future

object HeroRepoMock extends HeroRepo {

  private val heroesList = Seq(
    Hero(1, "Anti-Mage", "agi", "antimage_full.png", "magina story here"),
    Hero(2, "Axe", "str", "axe_full.png", "axe story here"),
    Hero(3, "Bane", "int", "bane_full.png", "bane story here"),
    Hero(4, "Bloodseeker", "agi", "bloodseeker_full.png", "strygwyr story here"),
    Hero(5, "Crystal Maiden", "int", "crystal_maiden_full.png", "rylai story here")
  )

  override def getAll: Future[Seq[Hero]] = Future.successful(heroesList)

  override def get(id: Int): Future[Option[Hero]] = Future.successful(heroesList.find(_.id == id))

}
