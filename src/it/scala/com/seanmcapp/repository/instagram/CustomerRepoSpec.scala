package com.seanmcapp.repository.instagram

import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

class CustomerRepoSpec extends AsyncWordSpec with Matchers {

  "should return customer" in {
    val response = CustomerRepoImpl.get(186373768)
    response.map { res =>
      res.map(_.name) shouldEqual Some("Rahmat Rasyidi Hakim")
    }
  }

  "insert function should properly inserted data into DB" in {
    val customer = Customer(123L, "Pawas", 1)
    val response = CustomerRepoImpl.insert(customer)
    response.map { res =>
      res shouldBe 1
    }
  }

  "update function should properly updated data into DB" in {
    val customer = Customer(123L, "Pawas", 1000)
    val response = CustomerRepoImpl.update(customer)
    response.map { res =>
      CustomerRepoImpl.delete(customer)
      res shouldBe 1
    }
  }

}
