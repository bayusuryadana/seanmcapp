package com.seanmcapp

import com.seanmcapp.repository._
import org.scalatest.{AsyncFlatSpec, Matchers}

class RepositorySpec extends AsyncFlatSpec with Matchers {

  DBGenerator.generate

  "Photo repo" should "get all id" in {
    PhotoRepo.getAll.map { res =>
      res.size shouldBe DBGenerator.photoData.size
    }
  }

  "Photo repo" should "get latest" in {
    PhotoRepo.getLatest.map { res =>
      res.map(_.id) should contain("1734075033692644433")
    }
  }

  "Photo repo" should "get random" in {
    PhotoRepo.getRandom.map { res =>
      res shouldNot equal (None)
    }
  }

  "Customer repo" should "get id" in {
    val customerId = 98387528L
    CustomerRepo.get(customerId).map { res =>
      res.map(_.name) should contain("Krisna Dibyo")
    }
  }

  "Customer repo" should "get all subscribed customer" in {
    CustomerRepo.getAllSubscribedCust.map { res =>
      res.size shouldBe 4
    }
  }

}
