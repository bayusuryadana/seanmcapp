package com.seanmcapp.repository.instagram

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class Photo(id: Long, thumbnailSrc: String, date: Long, caption: String, account: String)

class PhotoInfo(tag: Tag) extends Table[Photo](tag, "photos") {
  val id = column[Long]("id", O.PrimaryKey)
  val thumbnailSrc = column[String]("thumbnail_src")
  val date = column[Long]("date")
  val caption = column[String]("caption")
  val account = column[String]("account")

  def * = (id, thumbnailSrc, date, caption, account) <> (Photo.tupled, Photo.unapply)
}

trait PhotoRepo {

  def getAll: Future[Seq[Photo]]

  def get(id: Long): Future[Option[Photo]]

  def getRandom: Future[Option[Photo]]

  def insert(photos: Seq[Photo]): Future[Option[Int]]

}

object PhotoRepoImpl extends TableQuery(new PhotoInfo(_)) with PhotoRepo with DBComponent {

  def getAll: Future[Seq[Photo]] = {
    run(this.result)
  }

  def get(id: Long): Future[Option[Photo]] = {
    run(this.filter(_.id === id).result.headOption)
  }

  def getRandom: Future[Option[Photo]] = {
    run(this.sortBy(_ => SimpleFunction.nullary[Double]("random")).result.headOption)
  }

  def insert(photos: Seq[Photo]): Future[Option[Int]] = run((this ++= photos).asTry).map {
    case Failure(ex) => throw new Exception(ex.getMessage)
    case Success(value) => value
  }

  // this function is only for testing
  def delete(photos: Seq[Long]): Future[Int] = {
    run(this.filter(_.id inSet photos).delete)
  }

}
