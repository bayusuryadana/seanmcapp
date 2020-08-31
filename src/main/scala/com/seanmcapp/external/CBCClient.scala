package com.seanmcapp.external

import java.util.concurrent.TimeUnit

import com.seanmcapp.StorageConf
import com.seanmcapp.util.MemoryCache
import scalacache.Cache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import scala.concurrent.duration.{Duration, FiniteDuration}

class CBCClient(http: HttpRequestClient) extends MemoryCache {

  implicit val recommendationCache: Cache[Map[Long, Array[Long]]] = createCache[Map[Long, Array[Long]]]
  private val duration: FiniteDuration = Duration(1, TimeUnit.DAYS)

  val storageConf: StorageConf = StorageConf()

  def getRecommendation: Map[Long, Array[Long]] = {
    memoizeSync(Some(duration)) {
      val url = s"${storageConf.host}/${storageConf.bucket}/knn.csv"
      http.sendGetRequest(url).split("\n").map { line =>
        val row = line.split(",")
        row.head.toLong -> row.tail.map(_.toLong)
      }.toMap
    }
  }

}
