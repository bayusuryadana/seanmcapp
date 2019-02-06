package com.seanmcapp.repository.instagram

import scala.concurrent.Future

case class Photo(id: Long, thumbnailSrc: String, date: Long, caption: String, account: String)

trait PhotoRepo {

  def getAll: Future[Seq[Photo]]

  def getAll(account: String): Future[Seq[(Long, Long)]]

  def getLatest: Future[Option[Photo]]

  def getRandom(account: Option[String] = None): Future[Option[Photo]]

  def insert(photos: Seq[Photo]): Future[Option[Int]]

}
