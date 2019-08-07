package com.seanmcapp.repository.instagram

import org.scalatest.{AsyncWordSpec, Matchers}

class PhotoRepoSpec extends AsyncWordSpec with Matchers {

  "should return all photo" in {
    val response = PhotoRepoImpl.getAll
    response.map { res =>
      res.size shouldEqual 15
      res.headOption.map(_.caption) shouldEqual Some("Raihanah Yasmin. Vokasi'12")
    }
  }

  "should return random photo" in {
    val response = PhotoRepoImpl.getRandom(None)
    response.map { res =>
      res should not be None
    }
  }

  "should return not return photo from particular account" in {
    val response = PhotoRepoImpl.getRandom(Some("ugmcantik"))
    response.map { res =>
      res shouldBe None
    }
  }

  "should return photo from particular account" in {
    val response = PhotoRepoImpl.getRandom(Some("ui.cantik"))
    response.map { res =>
      res should not be None
      res.map(_.account) shouldEqual Some("ui.cantik")
    }
  }

  "insert function" in {
    // TODO: insert testing
    true shouldBe true
  }

}
