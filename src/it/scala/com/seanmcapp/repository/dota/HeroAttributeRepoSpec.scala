package com.seanmcapp.repository.dota

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class HeroAttributeRepoSpec extends AsyncWordSpec with Matchers {

  "should return corresponding hero given their id" in {
    val response = HeroAttributeRepoImpl.get(84) // Ogre Magi
    response.map { res =>
      res.map(_.baseHealthRegen) shouldEqual Some(3.25)
    }
  }

  "should not return corresponding hero given wrong id" in {
    val response = HeroAttributeRepoImpl.get(999)
    response.map { res =>
      res.map(_.baseHealthRegen) shouldEqual None
    }
  }

}
