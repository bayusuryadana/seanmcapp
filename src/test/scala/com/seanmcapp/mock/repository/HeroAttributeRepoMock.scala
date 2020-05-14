package com.seanmcapp.mock.repository

import com.seanmcapp.repository.dota.{HeroAttribute, HeroAttributeRepo}

import scala.concurrent.Future

object HeroAttributeRepoMock extends HeroAttributeRepo {

  private val attributesList = List(
    HeroAttribute(18,	200,	0.0,	75,	0,	1,	25,	41,	43,	22,	21,	16,	3.2,	2,	1.3,	150,	0,	1.8,	315,	0.6,	true),
    HeroAttribute(26,	200,	0.0,	75,	0,	-1,	25,	29,	35,	18,	15,	18,	2.2,	1.5,	3.5,	600,	900,	1.7,	290,	0.5,	true)
  )

  override def getAll: Future[Seq[HeroAttribute]] = Future.successful(attributesList)

  override def insertOrUpdate(attributes: Seq[HeroAttribute]): Seq[Future[Int]] = attributes.map(_ => Future.successful(1))

}
