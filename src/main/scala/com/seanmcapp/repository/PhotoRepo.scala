package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

case class Photo(id: String, thumbnailSrc: String, date: Long, caption: String, account: String)

class PhotoInfo(tag: Tag) extends Table[Photo](tag, "photos") {
  val id = column[String]("id", O.PrimaryKey)
  val thumbnailSrc = column[String]("thumbnail_src")
  val date = column[Long]("date")
  val caption = column[String]("caption")
  val account = column[String]("account")

  def * = (id, thumbnailSrc, date, caption, account) <> (Photo.tupled, Photo.unapply)
}

object PhotoRepo extends TableQuery(new PhotoInfo(_)) with DBComponent {

  def getAll: Future[Set[String]] = {
    run(this.map(_.id).result).map(_.toSet) //TODO: masukin cache
  }

  def getLatest: Future[Option[Photo]] = {
    run(this.sortBy(_.date.desc).take(1).result.headOption)
  }

  def getRandom: Future[Option[Photo]] = {
    for {
      size <- run(this.size.result) //TODO: masukin cache
      result <- run(this.drop(Random.nextInt(size)).result.headOption)
    } yield result
  }

  def update(photo: Photo): Future[Option[Photo]] = {
    run(this.returning(this).insertOrUpdate(photo))
  }

}
