package com.seanmcapp.repository.mongodb

import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import com.seanmcapp.repository.{Photo, PhotoRepo}
import org.mongodb.scala.Completed

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

class PhotoRepoImpl extends DBComponent[Photo]("photos") with PhotoRepo {

  override def getAll(): Future[Seq[Photo]] = collection.find().toFuture()

  override def getAll(account: String): Future[Set[Long]] = collection.find(equal("account", account)).toFuture().map(_.map(_.id).toSet)

  override def getLatest: Future[Option[Photo]] = collection.find().sort(orderBy(descending("date"))).first().toFutureOption()

  override def getLatest(account: String): Future[Option[Photo]] = collection.find(equal("account", account)).sort(orderBy(descending("date"))).first().toFutureOption()

  override def getRandom: Future[Option[Photo]] = for {
    size <- collection.countDocuments().toFuture()
    result <- collection.find().skip(Random.nextInt(size.toInt)).first().toFutureOption()
  } yield result

  override def getRandom(account: String): Future[Option[Photo]] = for {
    size <- collection.countDocuments(equal("account", account)).toFuture()
    result <- collection.find(equal("account", account)).skip(Random.nextInt(size.toInt)).first().toFutureOption()
  } yield result

  override def insert(photos: Seq[Photo]): Future[Completed] = collection.insertMany(photos).toFuture()

}
