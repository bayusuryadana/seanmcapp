package com.seanmcapp.repository

import org.mongodb.scala.Completed
import org.mongodb.scala.bson.annotations.BsonProperty
import org.mongodb.scala.result.DeleteResult

import scala.concurrent.Future

case class Photo(@BsonProperty("_id") id: Long, @BsonProperty("thumbnail_src") thumbnailSrc: String, date: Long, caption: String, account: String)

trait PhotoRepo {

  def getAll: Future[Seq[Photo]]

  def getAll(account: String): Future[Set[Long]]

  def getLatest: Future[Option[Photo]]

  def getLatest(account: String): Future[Option[Photo]]

  def getRandom: Future[Option[Photo]]

  def getRandom(account: String): Future[Option[Photo]]

  def insert(photos: Seq[Photo]): Future[Completed]

  def delete(id: Long): Future[DeleteResult]

}
