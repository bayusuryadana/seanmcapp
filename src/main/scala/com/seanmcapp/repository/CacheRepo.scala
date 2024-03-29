package com.seanmcapp.repository

import enumeratum.{Enum, EnumEntry}
import slick.jdbc.PostgresProfile.api._

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class Cache(feature: String, accountId: String, value: String, expiry: Option[Long])

class CacheInfo(tag: Tag) extends Table[Cache](tag, "caches") {
  val feature = column[String]("feature")
  val accountId = column[String]("account_id")
  val value = column[String]("value")
  val expiry = column[Option[Long]]("expiry")

  def * = (feature, accountId, value, expiry) <> (Cache.tupled, Cache.unapply)
}

trait CacheRepo {
  
  def getAll(): Future[Seq[Cache]]

  def get(feature: String, accountId: String): Future[Set[String]]

  def getMultiple(feature: String, accountId: String): Future[Set[String]]

  def insert(cache: Seq[Cache]): Future[Seq[Int]]

  def set(cache: Cache): Future[Int]

  def delete(feature: String, value: String): Future[Int]

}

object CacheRepoImpl extends TableQuery(new CacheInfo(_)) with CacheRepo with DBComponent {

  def getAll(): Future[Seq[Cache]] = run(this.result)

  def get(feature: String, accountId: String): Future[Set[String]] = {
    run(this.filter(r => r.feature === feature && r.accountId === accountId).result.headOption).map { resF =>
      resF.map(res => res.value.split(",").toSet).getOrElse {
        println(s"[INFO] cache is empty for feature: $feature and account_id: $accountId")
        Set.empty[String]
      }
    }
  }
  
  def getMultiple(feature: String, accountId: String): Future[Set[String]] = {
    run(this.filter(r => r.feature === feature && r.accountId === accountId).result).map { resF =>
      resF.map(_.value).toSet
    }
  }
  
  def insert(cache: Seq[Cache]): Future[Seq[Int]] = run(this ++= cache).map(_.toSeq)
  

  def set(cache: Cache): Future[Int] = {
    run(this.filter(r => r.feature === cache.feature && r.accountId === cache.accountId).update(cache))
  }

  def delete(feature: String, value: String): Future[Int] = run(this
    .filter(r => r.feature === feature && r.value === value).delete)

}

// $COVERAGE-OFF$
sealed abstract class FeatureType(val i: String) extends EnumEntry with Serializable {
  override def equals(obj: Any): Boolean = obj match {
    case a: FeatureType => a.i == i
    case _ => false
  }
}

object FeatureTypes extends Enum[FeatureType] {
  override def values: immutable.IndexedSeq[FeatureType] = findValues
  val fields = values.map(x => (x.i, x)).toMap

  lazy val getType: String => FeatureType = fields.getOrElse(_, FeatureTypes.Unknown)

  def apply(value: String): FeatureType = fields.getOrElse(value, Unknown)

  case object Deactivated extends FeatureType("deactivated")
  case object Unknown extends FeatureType("unknown")
  case object InstaStory extends FeatureType("instastory")
  case object InstaPost extends FeatureType("instapost")
  case object TwitLiked extends FeatureType("twitliked")
  case object Tweet extends FeatureType("tweet")
}