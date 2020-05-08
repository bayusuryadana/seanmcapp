package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepo}

import scala.concurrent.Future

object HeroRepoMock extends HeroRepo {

  private val heroesList = List(
    Hero(18, "Anti-Mage", "agi", "Melee", "Carry,Escape,Nuker", "antimage_full.png", "antimage_icon.png", "magina story here"),
    Hero(26, "Axe", "str", "Melee", "Initiator,Durable,Disabler,Jungler", "axe_full.png", "axe_icon.png", "axe story here"),
  )

  override def getAll: Future[Seq[Hero]] = Future.successful(heroesList)

  override def get(id: Int): Future[Option[Hero]] = Future.successful(heroesList.find(_.id == id))

  override def insertOrUpdate(heroes: Seq[Hero]): Seq[Future[Int]] = heroes.map(_ => Future.successful(1))

}
