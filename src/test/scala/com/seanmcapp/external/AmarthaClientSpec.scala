package com.seanmcapp.external

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class AmarthaClientSpec extends AnyWordSpec with Matchers {

  import AmarthaEndpoint._
  val detailResponseMap = List(/*793917, 793142, 755750, 759541, */753724).map { id =>
    baseUrl + details + id -> Source.fromResource(s"amartha/mitra-$id.json").mkString
  }.toMap
  val responseMap = Map(
    baseUrl + auth -> Source.fromResource("amartha/auth.json").mkString,
    baseUrl + allSummary -> Source.fromResource("amartha/summary.json").mkString,
    baseUrl + listMitra -> Source.fromResource("amartha/list-mitra.json").mkString,
    baseUrl + transaction -> Source.fromResource("amartha/transaction.json").mkString
  ) ++ detailResponseMap

  val amarthaService = new AmarthaClient(new HttpRequestClientMock(responseMap))

  "getAllSummary" in {
    val result = amarthaService.getAllSummary("access_token")
    val expected = AmarthaSummary(None, 0, 0, 0, 0, 0)
    result shouldBe expected
  }

  "getMitraList" in {
    val result = amarthaService.getMitraList("access_token")
    val expected = AmarthaMitraIdList(List(
      AmarthaPortofolio(50, "A", "2020-07-30T09:00:00+07:00", true, true, 793917, "ADEM SARI", 4500000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-07-30T07:00:00+07:00", true, true, 793142, "NUR AFI ", 4500000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-07-30T09:00:00+07:00", true, true, 755750, "EHEK ", 4000000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-07-30T09:00:00+07:00", true, true, 759541, "CRYSTAL", 4000000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(50, "A", "2020-06-17T07:00:00+07:00", true, true, 753724, "RUBY " , 4000000, "ONTIME",
        "https://pic1.jpg", "https://pic2.jpg")
    ))
    result shouldBe expected
  }

  "getMitraDetail" in {
    val result = amarthaService.getMitraDetail("access_token",753724)
    val expected = AmarthaDetail(List(
      AmarthaInstallment("2020-06-24T15:13:26.167066+07:00", 1, "NORMAL"),
      AmarthaInstallment("2020-07-01T14:39:51.568217+07:00", 1, "NORMAL"),
      AmarthaInstallment("2020-07-08T14:55:45.380834+07:00", 1, "NORMAL")
    ),
      AmarthaSummaryDetail(460000))
    result shouldBe expected
  }

  "getTransaction" in {
    val result = amarthaService.getTransaction("access_token")
    val expected = List(
      AmarthaTransaction("0", "13 Aug 2020", "90.800", "Imbal Hasil"),
      AmarthaTransaction("0", "13 Aug 2020", "90.800", "Imbal Hasil"),
      AmarthaTransaction("0", "13 Aug 2020", "0", "Imbal Hasil"),
      AmarthaTransaction("0", "13 Aug 2020", "90.800", "Imbal Hasil"),
      AmarthaTransaction("0", "13 Aug 2020", "89.200", "Imbal Hasil")
    )
    result shouldBe expected
  }

  "getTokenAuth" in {
    val result = amarthaService.getTokenAuth("username", "password")
    val expected = AmarthaAuthData("access_token", true, "", "Bayu Suryadana")
    result shouldBe expected
  }

}
