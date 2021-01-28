package com.seanmcapp.repository

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class RedisRepoSpec extends AsyncWordSpec with Matchers {

  private val testKey: String = "key"
  private val testValue: String = "value"

  "should return success set" in {
    val response = RedisRepoImpl.set(testKey, testValue)
    response shouldBe true
  }

  "(cont.) should return get from set value above" in {
    val response = RedisRepoImpl.get(testKey)
    response.nonEmpty shouldBe true
    response.get shouldBe testValue
  }

}
