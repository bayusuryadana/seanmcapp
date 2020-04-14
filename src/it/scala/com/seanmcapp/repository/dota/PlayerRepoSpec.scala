package com.seanmcapp.repository.dota

import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

class PlayerRepoSpec extends AsyncWordSpec with Matchers {

  "should return all player" in {
    val response = PlayerRepoImpl.getAll
    response.map { res =>
      res.size shouldEqual 11
      res.sortBy(_.id).headOption.map(_.personaName) shouldEqual Some("hnymnky")
    }
  }

  "should return corresponding player given their id" in {
    val response = PlayerRepoImpl.get(425114145)
    response.map { res =>
      res.map(_.personaName) shouldBe Some("einjineer")
    }
  }

  "should not return corresponding player given wrong id" in {
    val response = PlayerRepoImpl.get(999)
    response.map { res =>
      res shouldBe None
    }
  }

}
