package com.seanmcapp.repository

import scala.concurrent.Future

object CacheRepoMock extends CacheRepo {

  val cacheList: Map[String, String] = Map("key" -> "value")

  def getAll(): Future[Seq[Cache]] = Future.successful(cacheList.toSeq.map { case (k, v) => Cache(k, v, None)})

  def get(key: String): Future[Option[Cache]] =
    Future.successful(cacheList.get(key).map(value => Cache(key, value, None)))

  def set(cache: Cache): Future[Int] = Future.successful(1)

  def delete(key: String): Future[Int] = Future.successful(1)

}
