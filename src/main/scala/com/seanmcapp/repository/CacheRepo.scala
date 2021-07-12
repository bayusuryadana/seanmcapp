package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class Cache(key: String, value: String, expiry: Option[Long])

class CacheInfo(tag: Tag) extends Table[Cache](tag, "caches") {
  val key = column[String]("key", O.PrimaryKey)
  val value = column[String]("value")
  val expiry = column[Option[Long]]("expiry")

  def * = (key, value, expiry) <> (Cache.tupled, Cache.unapply)
}

trait CacheRepo {

  def getAll(): Future[Seq[Cache]]

  def get(key: String): Future[Option[Cache]]

  def set(cache: Cache): Future[Int]

  def delete(key: String): Future[Int]

}

object CacheRepoImpl extends TableQuery(new CacheInfo(_)) with CacheRepo with DBComponent {

  def getAll(): Future[Seq[Cache]] = run(this.result)

  def get(key: String): Future[Option[Cache]] = run(this.filter(_.key === key).result.headOption)

  def set(cache: Cache): Future[Int] = {
      // try insert first
      run((this += cache).asTry).flatMap {
        case Failure(ex) => // TODO: need better checking to do upsert
          // else try update
          ex.printStackTrace()
          run((this.filter(_.key === cache.key).update(cache)).asTry).map {
            case Failure(ex2) =>
              ex2.printStackTrace()
              throw new Exception("Failed to insert/update cache")
            case Success(value) => value
          }
        case Success(value) =>
          Future.successful(value)
      }
  }

  def delete(key: String): Future[Int] = run(this.filter(_.key === key).delete)

}
