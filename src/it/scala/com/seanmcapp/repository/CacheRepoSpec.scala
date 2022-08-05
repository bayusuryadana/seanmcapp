package com.seanmcapp.repository

import org.joda.time.DateTime
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class CacheRepoSpec extends AsyncWordSpec with Matchers {

  private val key1: String = "key1"
  private val accountId1: String = "123"
  private val value1: String = "value1"
  private val value11: String = "value11"
  private val key2: String = "key2"
  private val value2: String = "value2"
  private val value22: String = "value22"
  private val accountId2: String = "456"

  "should return success insert" in {
    val epochTime = new DateTime().plusSeconds(5).getMillis / 1000
    val cache1 = Cache(key1, accountId1, value1, None)
    val cache2 = Cache(key2, accountId2, value2, Some(epochTime))
    val response1F = CacheRepoImpl.insert(Seq(cache1))
    val response2F = CacheRepoImpl.insert(Seq(cache2))
    for {
      res1 <- response1F
      res2 <- response2F
    } yield {
      res1 shouldBe List(1)
      res2 shouldBe List(1)
    }
  }

  "should return success set" in {
    val epochTime = new DateTime().plusSeconds(5).getMillis / 1000
    val cache1 = Cache(key1, accountId1, value11, None)
    val cache2 = Cache(key2, accountId2, value22, Some(epochTime))
    val response1F = CacheRepoImpl.set(cache1)
    val response2F = CacheRepoImpl.set(cache2)
    for {
      res1 <- response1F
      res2 <- response2F
    } yield {
      res1 shouldBe 1
      res2 shouldBe 1
    }
  }

  "(cont.) should return get and getAll from set value above" in {
    val response0F = CacheRepoImpl.getAll()
    val response1F = CacheRepoImpl.get(key1, accountId1)
    val response2F = CacheRepoImpl.get(key2, accountId2)
    for {
      res0 <- response0F
      res1 <- response1F
      res2 <- response2F
    } yield {
      res0.size shouldBe 2
      res1 should contain theSameElementsAs Set("value11")
      res2 should contain theSameElementsAs Set("value22")
    }
  }

  "(cont.) should delete all value above" in {
    val response1F = CacheRepoImpl.delete(key1, value11)
    val response2F = CacheRepoImpl.delete(key2, value22)
    for {
      res1 <- response1F
      res2 <- response2F
    } yield {
      res1 shouldBe 1
      res2 shouldBe 1
    }
  }

}
