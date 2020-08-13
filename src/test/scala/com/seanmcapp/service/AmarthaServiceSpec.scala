package com.seanmcapp.service

import com.seanmcapp.mock.requestbuilder.HttpRequestBuilderMock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.io.Source

class AmarthaServiceSpec extends AsyncWordSpec with Matchers {
  import AmarthaEndpoint._
  "AmarthaService should return correctly" in {
    val detailResponseMap = List(793917, 793142, 755750, 759541, 753724).map { id =>
      baseUrl + details + id -> Source.fromResource(s"amartha/detail/$id.json").mkString
    }.toMap
    val responseMap = Map(
      baseUrl + auth -> Source.fromResource("amartha/auth.json").mkString,
      baseUrl + allSummary -> Source.fromResource("amartha/summary.json").mkString,
      baseUrl + listMitra -> Source.fromResource("amartha/list-mitra.json").mkString,
      baseUrl + transaction -> Source.fromResource("amartha/transaction.json").mkString
    ) ++ detailResponseMap

    val amarthaService = new AmarthaService(new HttpRequestBuilderMock(responseMap))

    val result = amarthaService.getAmarthaResult("username","password")
    val expected = Source.fromResource("amartha/expected.json").mkString
    result shouldBe expected
  }

}
