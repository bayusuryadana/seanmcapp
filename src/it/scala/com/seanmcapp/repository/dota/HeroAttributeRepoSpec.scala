package com.seanmcapp.repository.dota

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class HeroAttributeRepoSpec extends AsyncWordSpec with Matchers {

  "test creation dummy HeroAttribute" in {
    val heroAttr = HeroAttribute.dummy(-1)
    heroAttr shouldEqual HeroAttribute(-1, 0, 0d, 0, 0d, 0, 0, 0, 0, 0, 0, 0, 0d, 0d, 0d, 0, 0, 0d, 0, 0d, false)
  }

  "should return all the hero in DB" in {
    val response = HeroAttributeRepoImpl.getAll
    response.map { res =>
      res.length shouldEqual 119
      res.sortBy(_.id).headOption.map(_.baseHealthRegen) shouldEqual Some(0.25)
    }
  }

  "should return successful insertion" in {
    val heroList = Seq(
      HeroAttribute(999, 0, 0d, 0, 0d, 0, 0, 0, 0, 0, 0, 0, 0d, 0d, 0d, 0, 0, 0d, 0, 0d, false)
    )
    val response = HeroAttributeRepoImpl.insertOrUpdate(heroList)
    Future.sequence(response).map { res =>
      res shouldEqual Seq(1)
    }
  }

}
