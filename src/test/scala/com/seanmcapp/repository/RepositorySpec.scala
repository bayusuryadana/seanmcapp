package com.seanmcapp.repository

import com.seanmcapp.DBGenerator
import com.seanmcapp.startup.Injection
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

class RepositorySpec extends WordSpec with Matchers with Injection {

  DBGenerator.generate

  val photoId = "1734075033692644433"
  val customerId = 98387528L

  "Photo repo should get all id" in {
    photoRepoImpl.getAll("ui.cantik").map { res =>
      res.size shouldBe 8
    }
  }

  "Photo repo should get latest" in {
    photoRepoImpl.getLatest.map { res =>
      res.map(_.id) should contain(photoId)
    }
  }

  "Photo repo should get random" in {
    photoRepoImpl.getRandom.map { res =>
      res shouldNot equal (None)
    }
  }

  "Customer repo should get id" in {
    customerRepoImpl.get(customerId).map { res =>
      res.map(_.name) should contain("Krisna Dibyo")
    }
  }

  "Customer repo should get all subscribed customer" in {
    customerRepoImpl.getAllSubscribedCust.map { res =>
      res.size shouldBe 4
    }
  }

  // all update method can't be tested due to limited slick capability

}
