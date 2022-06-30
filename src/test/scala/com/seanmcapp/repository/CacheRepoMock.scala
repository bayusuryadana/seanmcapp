package com.seanmcapp.repository

import scala.concurrent.Future

object CacheRepoMock extends CacheRepo {

  val cacheList: List[Cache] = List(
    Cache("feature","123", "value", None)
  )

  def getAll(): Future[Seq[Cache]] = Future.successful(cacheList)

  def get(feature: String, accountId: String): Future[Set[String]] =
    Future.successful(cacheList.find(r => r.feature == feature && r.accountId == accountId).map(_.value).toSet)

  def set(cache: Cache): Future[Int] = Future.successful(1)

  def delete(feature: String, accountId: String): Future[Int] = Future.successful(1)

}
