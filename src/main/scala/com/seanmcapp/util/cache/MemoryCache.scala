package com.seanmcapp.util.cache

import com.google.common.cache.CacheBuilder
import scalacache.{Cache, Entry}
import scalacache.guava.GuavaCache

trait MemoryCache {
  protected def createCache[T]: Cache[T] = {
    val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
    GuavaCache(underlyingGuavaCache)
  }
}