package com.seanmcapp.external

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class AmarthaClientSpec extends AnyWordSpec with Matchers {

  import AmarthaEndpoint._
  val responseMap = Map(
    baseUrl + auth -> Source.fromResource("amartha/auth.json").mkString,
    baseUrl + transaction -> Source.fromResource("amartha/transaction.json").mkString
  )

  val amarthaService = new AmarthaClient(new HttpRequestClientMock(responseMap))

  "getTransaction" in {
    val result = amarthaService.getTransaction("access_token")
    val expected = List(
      AmarthaTransaction("0", "13 Aug 2020", "90.800", "Imbal Hasil", "17.744.750"),
      AmarthaTransaction("0", "13 Aug 2020", "90.800", "Imbal Hasil", "17.653.950"),
      AmarthaTransaction("0", "13 Aug 2020", "0", "Imbal Hasil", "17.563.150"),
      AmarthaTransaction("0", "13 Aug 2020", "90.800", "Imbal Hasil", "17.563.150"),
      AmarthaTransaction("0", "13 Aug 2020", "89.200", "Imbal Hasil", "17.472.350")
    )
    result shouldBe expected
  }

  "getTokenAuth" in {
    val result = amarthaService.getTokenAuth("username", "password")
    val expected = AmarthaAuthData("access_token", true, "", "Bayu Suryadana")
    result shouldBe expected
  }

}
