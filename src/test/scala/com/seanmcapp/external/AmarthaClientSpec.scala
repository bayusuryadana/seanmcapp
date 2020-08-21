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
    val expected = AmarthaSummary(None, 0, 0, 0, 0, 0, 0, 0, 0.0, 0, 0, 0, 0, 0)
    result shouldBe expected
  }

  "getMitraList" in {
    val result = amarthaService.getMitraList("access_token")
    val expected = AmarthaMitraIdList(List(
      AmarthaPortofolio(Some(""), Some(""), "2020-07-29T14:30:01.462989+07:00", "A", "2020-07-30T09:00:00+07:00", None,
        true, false, true, None, 793917, "ADEM SARI", 4500000, None, "Modal Beli Pupuk", 507150, None, Some(""),
        "ONTIME", "2020-07-15T00:00:00+07:00", "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(Some(""), Some(""), "2020-07-29T14:30:01.462989+07:00", "A", "2020-07-30T07:00:00+07:00", None,
        true, false, true, None, 793142, "NUR AFI ", 4500000, None, "Modal Dagang Warung Kecil", 507150, None, Some(""),
        "ONTIME", "2020-07-14T00:00:00+07:00", "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(Some(""), Some(""), "2020-07-29T14:30:01.462989+07:00", "A", "2020-07-30T09:00:00+07:00", None,
        true, false, true, None, 755750, "EHEK ", 4000000, None, "Modal Dagang Pakaian", 450800, None, Some(""),
        "ONTIME", "2020-04-08T00:00:00+07:00", "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(Some(""), Some(""), "2020-07-29T14:30:01.462989+07:00", "A", "2020-07-30T09:00:00+07:00", None,
        true, false, true, None, 759541, "CRYSTAL", 4000000, None, "Modal Dagang Pakaian", 441600, None, Some(""),
        "ONTIME", "2020-04-08T00:00:00+07:00", "https://pic1.jpg", "https://pic2.jpg"),
      AmarthaPortofolio(Some(""), Some(""), "2020-06-12T15:07:39.185207+07:00", "A", "2020-06-17T07:00:00+07:00", None,
        true, false, true, None, 753724, "RUBY " , 4000000, None, "Modal Dagang Perabot", 386400, None, Some(""),
        "ONTIME", "2020-04-07T00:00:00+07:00", "https://pic1.jpg", "https://pic2.jpg")
    ))
    result shouldBe expected
  }

  "getMitraDetail" in {
    val result = amarthaService.getMitraDetail("access_token",753724)
    val expected = AmarthaDetail(List(
      AmarthaInstallment("2020-06-24T15:13:26.167066+07:00", 1, "NORMAL", false, 9200, 80000),
      AmarthaInstallment("2020-07-01T14:39:51.568217+07:00", 1, "NORMAL", false, 9200, 80000),
      AmarthaInstallment("2020-07-08T14:55:45.380834+07:00", 1, "NORMAL", false, 9200,80000)
    ),
    AmarthaLoan(753724, "Lampung Timur", "Kota Metro", "2021-06-02T07:00:00+07:00", "LAMPUNG", "Rabu", "Perdagangan"))
    result shouldBe expected
  }

  "getTransaction" in {
    val result = amarthaService.getTransaction("access_token")
    val expected = List(
      AmarthaTransaction(Some(""), "0", "13 Aug 2020", "90.800", "754398", "27622898", "Imbal Hasil"),
      AmarthaTransaction(Some(""), "0", "13 Aug 2020", "90.800", "754538", "27622897", "Imbal Hasil"),
      AmarthaTransaction(Some(""), "0", "13 Aug 2020", "0", "451193", "27622896", "Imbal Hasil"),
      AmarthaTransaction(Some(""), "0", "13 Aug 2020", "90.800", "745870", "27622895", "Imbal Hasil"),
      AmarthaTransaction(Some(""), "0", "13 Aug 2020", "89.200", "777309", "27622894", "Imbal Hasil")
    )
    result shouldBe expected
  }

  "getTokenAuth" in {
    val result = amarthaService.getTokenAuth("username", "password")
    val expected = AmarthaAuthData("access_token", true, "", "Bayu Suryadana")
    result shouldBe expected
  }

}
