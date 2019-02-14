package com.seanmcapp.util.requestbuilder

import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder
import scalacache._
import scalacache.guava._
import scalacache.memoization._
import scalacache.modes.sync._
import scalaj.http.{Http, HttpResponse}

import scala.concurrent.duration.Duration

trait DotaRequest {

  val baseUrl = "https://api.opendota.com/api/players/"

  val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[HttpResponse[String]]]
  implicit val guavaCache: Cache[HttpResponse[String]] = GuavaCache(underlyingGuavaCache)

  def getMatches(id: Long): HttpResponse[String] = {
    Http(baseUrl + id + "/matches").asString
  }

  def getPeers(id: Long): HttpResponse[String] = {
    memoizeSync(Some(Duration(2, TimeUnit.HOURS))) {
      println("ehek")
      Http(baseUrl + id + "/peers").asString
    }
  }

}
