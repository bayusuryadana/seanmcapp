package com.seanmcapp.repository.dota

import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

class HeroRepoSpec extends AsyncWordSpec with Matchers {

  "should return all hero" in {
    val response = HeroRepoImpl.getAll
    response.map { res =>
      res.size shouldEqual 119
      res.sortBy(_.id).headOption.map(_.localizedName) shouldEqual Some("Anti-Mage")
    }
  }

}
