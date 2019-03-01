package com.seanmcapp.service

import com.seanmcapp.repository.instagram.{Customer, Photo}
import com.seanmcapp.{InjectionTest, ServiceImpl}
import org.scalatest.{AsyncWordSpec, Matchers}

class ServiceSpec extends AsyncWordSpec with Matchers with ServiceImpl with InjectionTest {

  val customer = Customer(1, "pawas", "telegram")

  "getRandom from individual" in {
    getRandom[Photo](customer, None, None)((p:Photo) => p).map { res =>
      res should not be None
    }
  }

  "getRandom from group" in {
    val group = Customer(-1, "OMOM", "telegram")
    getRandom[Photo](customer, Some(group), None)((p:Photo) => p).map { res =>
      res should not be None
    }
  }

  "getRandom filtered by account" in {
    val account = "ui.cantik"
    getRandom[Photo](customer, None, None)((p:Photo) => p).map { res =>
      res should not be None
      res.get.account shouldBe account
    }
  }
}
