package com.seanmcapp.repository

import com.seanmcapp.model.Photo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.jdbc.PostgresProfile.api._

import scala.util.Random

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
    db.run {
      this.map(_.id).result //TODO: masukin cache
    }.map(_.toSet)
  }

  def getLatest: Future[Option[Photo]] = {
    db.run {
      this.sortBy(_.date.desc).take(1).result.headOption
    }
  }

  def getRandom: Future[Option[Photo]] = {
    for {
      size <- db.run {
        this.size.result //TODO: masukin cache
      }
      result <- db.run {
        this.drop(Random.nextInt(size)).result.headOption
      }
    } yield result
  }

  def update(photo: Photo): Future[Option[Photo]] = {
    db.run {
      this.returning(this).insertOrUpdate(photo)
    }
  }

}
