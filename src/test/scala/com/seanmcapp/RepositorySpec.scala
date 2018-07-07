package com.seanmcapp

import com.seanmcapp.startup.Injection
import org.scalatest.{AsyncFlatSpec, Matchers}

class RepositorySpec extends AsyncFlatSpec with Matchers with Injection {

  DBGenerator.generate

  val photoId = "1734075033692644433"
  val customerId = 98387528L

  "Photo repo" should "get all id" in {
    photoRepo.getAll("ui.cantik").map { res =>
      res.size shouldBe 8
    }
  }

  "Photo repo" should "get latest" in {
    photoRepo.getLatest.map { res =>
      res.map(_.id) should contain(photoId)
    }
  }

  "Photo repo" should "get random" in {
    photoRepo.getRandom.map { res =>
      res shouldNot equal (None)
    }
  }

  "Customer repo" should "get id" in {
    customerRepo.get(customerId).map { res =>
      res.map(_.name) should contain("Krisna Dibyo")
    }
  }

  "Customer repo" should "get all subscribed customer" in {
    customerRepo.getAllSubscribedCust.map { res =>
      res.size shouldBe 4
    }
  }

  // all update method can't be tested due to limited slick capability

}
