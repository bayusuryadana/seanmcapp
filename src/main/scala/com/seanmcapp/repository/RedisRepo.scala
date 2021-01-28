package com.seanmcapp.repository

import java.net.URI

import com.redis.RedisClient
import com.seanmcapp.RedisConf

import scala.concurrent.duration.Duration

trait RedisRepo {

  def set(key: String, value: String, expire: Option[Duration] = None): Boolean

  def get(key: String): Option[String]

}

object RedisRepoImpl extends RedisRepo {

  private val redisConf = RedisConf()

  private val redisClient = new RedisClient(new URI(redisConf.connectionUrl))

  override def set(key: String, value: String, expire: Option[Duration] = None): Boolean =
    redisClient.set(key, value, expire = expire.orNull)

  override def get(key: String): Option[String] = redisClient.get[String](key)

}