package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{HeroAttribute, HeroAttributeRepo}

import scala.concurrent.Future

object HeroAttributeRepoMock extends HeroAttributeRepo {

  private val attributesList = List(
    HeroAttribute(18,200,0.25,75,0.0,-1,25,29,33,23,24,12,1.3,3.0,1.8,150,0,1.4,310,0.5,true)
  )

  override def get(id: Int): Future[Option[HeroAttribute]] = Future.successful(attributesList.find(_.id == id))

  override def insertOrUpdate(attributes: Seq[HeroAttribute]): Seq[Future[Int]] = attributes.map(_ => Future.successful(1))

}
