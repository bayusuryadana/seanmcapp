package com.seanmcapp.repository

import com.seanmcapp.repository.dota.{Hero, HeroRepoImpl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class HeroRepoSpec extends AsyncWordSpec with Matchers {

  "test creation dummy Hero" in {
    val hero = Hero.dummy(-1)
    hero shouldEqual Hero(-1, "Unknown", "???", "", "", "", "", "")
  }

  "should return all hero" in {
    val response = HeroRepoImpl.getAll
    response.map { res =>
      res.size shouldEqual 119
      res.sortBy(_.id).headOption.map(_.localizedName) shouldEqual Some("Anti-Mage")
    }
  }

  "should return successful insertion" in {
    val heroList = Seq(
      Hero(999, "TestHero", "???", "", "", "", "", "")
    )
    val response = HeroRepoImpl.insertOrUpdate(heroList)
    Future.sequence(response).map { res =>
      res shouldEqual Seq(1)
    }
  }

}
