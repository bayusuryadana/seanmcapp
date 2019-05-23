package com.seanmcapp.repository.instagram

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

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

  def getAll(account: String): Future[Seq[(Long, Long)]]

  def getLatest: Future[Option[Photo]]

  def getRandom(account: Option[String] = None): Future[Option[Photo]]

  def insert(photos: Seq[Photo]): Future[Option[Int]]

}

object PhotoRepoImpl extends TableQuery(new PhotoInfo(_)) with PhotoRepo with DBComponent {

  def getAll(account: String): Future[Seq[(Long, Long)]] = {
    run(this.filter(_.account === account).sortBy(_.date.desc).map(res => (res.id, res.date)).result)
  }

  def getLatest: Future[Option[Photo]] = {
    run(this.sortBy(_.date.desc).take(1).result.headOption)
  }

  def getRandom(account: Option[String] = None): Future[Option[Photo]] = {
    val rand = SimpleFunction.nullary[Double]("random")
    account match {
      case Some(accString:String) => run(this.filter(_.account === accString).sortBy(_ => rand).result.headOption)
      case _ => run(this.sortBy(_ => rand).result.headOption)
    }
  }

  def insert(photos: Seq[Photo]): Future[Option[Int]] = run(this ++= photos)

}
