package com.seanmcapp.repository.dota

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class HeroAttributeRepoSpec extends AsyncWordSpec with Matchers {

  "should return all the hero in DB" in {
    val response = HeroAttributeRepoImpl.getAll
    response.map { res =>
      res.length shouldEqual 119
      res.sortBy(_.id).headOption.map(_.baseHealthRegen) shouldEqual Some(0.25)
    }
  }

}
