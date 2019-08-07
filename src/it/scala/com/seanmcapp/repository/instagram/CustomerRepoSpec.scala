package com.seanmcapp.repository.instagram

import org.scalatest.{AsyncWordSpec, Matchers}

class CustomerRepoSpec extends AsyncWordSpec with Matchers {

  "should return customer" in {
    val response = CustomerRepoImpl.get(186373768)
    response.map { res =>
      res.map(_.name) shouldEqual Some("Rahmat Rasyidi Hakim")
    }
  }

  "insert function" in {
    // TODO: insert testing
    true shouldBe true
  }

  "update function" in {
    // TODO: update testing
    true shouldBe true
  }

}
