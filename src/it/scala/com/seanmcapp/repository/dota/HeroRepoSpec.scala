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

  "should return corresponding hero given their id" in {
    val response = HeroRepoImpl.get(35)
    response.map { res =>
      res.map(_.localizedName) shouldEqual Some("Sniper")
    }
  }

  "should not return corresponding hero given wrong id" in {
    val response = HeroRepoImpl.get(999)
    response.map { res =>
      res.map(_.localizedName) shouldEqual None
    }
  }

}
