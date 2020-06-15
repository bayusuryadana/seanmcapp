package com.seanmcapp

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest

class RouteSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val route = new Route().routePath

  "/" in {
    Get() ~> route ~> check {
      responseAs[String] shouldEqual "Life is a gift, keep smiling and giving goodness !"
    }
  }

//  "return OK for POST request to /broadcast" in {
//    val multipartForm =
//      Multipart.FormData(
//        Multipart.FormData.BodyPart.Strict(
//          "csv",
//          HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
//          Map("filename" -> "primes.csv")),
//        Multipart.FormData.BodyPart.Strict(
//          "comment",
//          HttpEntity("comment"))
//      )
//
//    Post("/broadcast", multipartForm) ~> route ~> check {
//      status shouldEqual StatusCodes.OK
//    }
//  }

}
