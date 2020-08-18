package com.seanmcapp.util

import com.google.common.cache.CacheBuilder
import scalacache.guava.GuavaCache
import scalacache.{Cache, Entry}

trait MemoryCache {
  protected def createCache[T]: Cache[T] = {
    val underlyingGuavaCache = CacheBuilder.newBuilder().maximumSize(10000L).build[String, Entry[T]]
    GuavaCache(underlyingGuavaCache)
  }
}
