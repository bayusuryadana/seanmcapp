package com.seanmcapp.repository
import scala.concurrent.duration.Duration

object RedisRepoMock extends RedisRepo {

  val redisList: Map[String, String] = Map(
    "key" -> "value"
  )

  override def set(key: String, value: String, expire: Option[Duration]): Boolean = true

  override def get(key: String): Option[String] = redisList.get(key)

}
