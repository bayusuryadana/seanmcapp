package com.seanmcapp.repository.mongodb

import org.mongodb.scala.model.Filters._
import com.seanmcapp.repository.{Track, TrackRepo}
import org.mongodb.scala.Completed

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TrackRepoImpl extends DBComponent[Track]("tracks") with TrackRepo {

  override def getCount: Future[Long] = collection.estimatedDocumentCount().toFuture()

  override def getLast100(customerId: Long): Future[Set[Long]] = collection
    .find(equal("customers_id", customerId)).limit(100).toFuture().map(_.map(_.photoId).toSet)

  override def insert(track: Track): Future[Completed] = collection.insertOne(track).toFuture()

}
