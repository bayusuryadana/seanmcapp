package com.seanmcapp.repository.instagram

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class PhotoInfo(tag: Tag) extends Table[Photo](tag, "photos") {
  val id = column[Long]("id", O.PrimaryKey)
  val thumbnailSrc = column[String]("thumbnail_src")
  val date = column[Long]("date")
  val caption = column[String]("caption")
  val account = column[String]("account")

  def * = (id, thumbnailSrc, date, caption, account) <> (Photo.tupled, Photo.unapply)
}

object PhotoRepoImpl extends TableQuery(new PhotoInfo(_)) with PhotoRepo with DBComponent {

  // TODO: get rid of this to Grafana
  def getAll: Future[Seq[Photo]] = {
    run(this.result)
  }

  def getAll(account: String): Future[Seq[(Long, Long)]] = {
    run(this.filter(_.account === account).sortBy(_.date.desc).map(res => (res.id, res.date)).result)
  }

  def getLatest: Future[Option[Photo]] = {
    run(this.sortBy(_.date.desc).take(1).result.headOption)
  }

  def getRandom(account: Option[String] = None): Future[Option[Photo]] = {
    val rand = SimpleFunction.nullary[Double]("rand")
    account match {
      case Some(s:String) => run(this.filter(_.account === account).sortBy(_ => rand).result.headOption)
      case _ => run(this.sortBy(_ => rand).result.headOption)
    }
  }

  // TODO: not tested
  def insert(photos: Seq[Photo]): Future[Option[Int]] = run(this ++= photos)

}
