package com.seanmcapp.repository.postgre

import com.seanmcapp.repository.{Photo, PhotoRepo}
import org.mongodb.scala.Completed
import org.mongodb.scala.result.DeleteResult
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

class PhotoInfo(tag: Tag) extends Table[Photo](tag, "photos") {
  val id = column[Long]("id", O.PrimaryKey)
  val thumbnailSrc = column[String]("thumbnail_src")
  val date = column[Long]("date")
  val caption = column[String]("caption")
  val account = column[String]("account")

  def * = (id, thumbnailSrc, date, caption, account) <> (Photo.tupled, Photo.unapply)
}

class PhotoRepoImpl extends TableQuery(new PhotoInfo(_)) with PhotoRepo with DBComponent {

  def getAll: Future[Seq[Photo]] = {
    run(this.result)
  }

  def getAll(account: String): Future[Set[Long]] = {
    run(this.filter(_.account === account).map(_.id).result).map(_.toSet)
  }

  def getLatest: Future[Option[Photo]] = {
    run(this.sortBy(_.date.desc).take(1).result.headOption)
  }

  def getLatest(account: String): Future[Option[Photo]] = {
    run(this.filter(_.account === account).sortBy(_.date.desc).take(1).result.headOption)
  }

  def getRandom: Future[Option[Photo]] = {
    for {
      size <- run(this.size.result)
      result <- run(this.drop(Random.nextInt(size)).result.headOption)
    } yield result
  }

  def getRandom(account: String): Future[Option[Photo]] = {
    for {
      size <- run(this.filter(_.account === account).size.result)
      result <- run(this.filter(_.account === account).drop(Random.nextInt(size)).result.headOption)
    } yield result
  }

  def update(photo: Photo): Future[Option[Photo]] = {
    run(this.returning(this).insertOrUpdate(photo))
  }

  def insert(photos: Seq[Photo]): Future[Completed] = ???

  def delete(id: Long): Future[DeleteResult] = ???
}
