package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepo}

import scala.concurrent.Future

object HeroRepoMock extends HeroRepo {

  private val heroesList = List(
    Hero(1, "Anti-Mage", "agi", "Melee", "Carry,Escape,Nuker", "antimage_full.png", "antimage_icon.png", "magina story here"),
    Hero(2, "Axe", "str", "Melee", "Initiator,Durable,Disabler,Jungler", "axe_full.png", "axe_icon.png", "axe story here"),
    Hero(3, "Bane", "int", "Ranged", "Support,Disabler,Nuker,Durable", "bane_full.png", "bane_icon.png", "bane story here"),
    Hero(4, "Bloodseeker", "agi", "Melee", "Carry,Disabler,Jungler,Nuker,Initiator", "bloodseeker_full.png", "bloodseeker_icon.png", "strygwyr story here"),
    Hero(5, "Crystal Maiden", "int", "Ranged", "Support,Disabler,Nuker,Jungler", "crystal_maiden_full.png", "crystal_maiden_icon.png", "rylai story here")
  )

  override def getAll: Future[Seq[Hero]] = Future.successful(heroesList)

  override def get(id: Int): Future[Option[Hero]] = Future.successful(heroesList.find(_.id == id))

  override def insertOrUpdate(heroes: Seq[Hero]): Seq[Future[Int]] = heroes.map(_ => Future.successful(1))

}
