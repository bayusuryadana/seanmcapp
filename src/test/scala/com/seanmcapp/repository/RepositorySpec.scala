package com.seanmcapp.repository

import com.seanmcapp.{DBGenerator, InjectionTest}
import com.seanmcapp.startup.Injection
import org.scalatest.{AsyncWordSpec, Matchers}

class RepositorySpec extends AsyncWordSpec with Matchers with InjectionTest {

  // all insert method can't be tested due to limited slick capability

  DBGenerator.generate

  val photoId = "1734075033692644433"
  val photoId2 = "1733941761435551783"
  val customerId = 98387528L

  "Photo repo should get all id" in {
    photoRepoImpl.getAll("ui.cantik").map { res =>
      res.size shouldBe 7
    }
  }

  "Photo repo should get latest" in {
    photoRepoImpl.getLatest.map { res =>
      res.map(_.id) should contain(photoId)
    }
  }

  "Photo repo should get latest with account filter" in {
    photoRepoImpl.getLatest("ugmcantik").map { res =>
      res.map(_.id) should contain("1733941761435551783")
    }
  }

  "Photo repo should get random" in {
    photoRepoImpl.getRandom.map { res =>
      res shouldNot equal (None)
    }
  }

  "Photo repo should update photo" in {
    val photo = Photo(photoId2, "1", 123, "Fawwaz Afifanto", "ui.jancok")
    photoRepoImpl.update(photo).map { res =>
      res should equal (None)
    }
  }

  /*
  "Photo repo should insert photo" in {
    val photo = Photo("1", "1", 123, "Fawwaz Afifanto", "ui.jancok")
    photoRepoImpl.update(photo).map { res =>
      res shouldNot equal (None)
    }
  }
  */

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

  "Customer repo should update customer" in {
    val customer = Customer(customerId, "Praw", true)
    customerRepoImpl.update(customer).map { res =>
      res should be (None)
    }
  }

  /*
  "Customer repo should insert customer" in {
    val customer = Customer(1, "Piccha", false)
    customerRepoImpl.update(customer).map { res =>
      res shouldNot be (None)
    }
  }
  */

  "Vote repo should update customer" in {
    val vote = Vote(customerId+":"+photoId, photoId, customerId, 5)
    voteRepoImpl.update(vote).map { res =>
      res should be (None)
    }
  }

  /*
  "Vote repo should insert customer" in {
    val vote = Vote(1+":"+2, "2", 1, 1)
    voteRepoImpl.update(vote).map { res =>
      res shouldNot be (None)
    }
  }
  */

  "Account repo should get all row" in {
    accountRepoImpl.getAll.map { res =>
      res.size shouldBe 1
    }
  }

}
