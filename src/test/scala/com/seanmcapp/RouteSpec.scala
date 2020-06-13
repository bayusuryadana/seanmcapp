package com.seanmcapp

import java.io.File

import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, Multipart, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.directives.FileInfo
import com.seanmcapp.util.parser.BroadcastOutput
import com.seanmcapp.util.parser.encoder.{RouteEncoder, TelegramResponse}
import spray.json._


class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest with RouteEncoder with Injection {

  def tempDestination(fileInfo: FileInfo): File =
    File.createTempFile(fileInfo.fileName, ".tmp")

  val route = List(
//    post((path("broadcast") & entity(as[Multipart.FormData]))(formData => storeUploadedFile("csv", tempDestination) {
//      case (metadata, file) =>
//        // do something with the file and file metadata ...
//        println(formData)
//        println(file.getAbsolutePath)
//        file.delete()
//        complete(StatusCodes.OK)
//    }))
//    post((path("broadcast") & entity(as[Multipart.FormData]))(
//      formData => complete(StatusCodes.OK)))
//
    post((path("broadcast") & entity(as[Multipart.FormData]))(
      _ => complete(broadcasterAPI.broadcastWithPhoto().map(res => encode[Option[BroadcastOutput]](res))))),
  ).reduce{ (a,b) => a~b }

  "Broadcaster Service" should {
    "return OK for POST request to /broadcast" in {
      val multipartForm =
        Multipart.FormData(
          Multipart.FormData.BodyPart.Strict(
            "csv",
            HttpEntity(ContentTypes.`text/plain(UTF-8)`, "2,3,5\n7,11,13,17,23\n29,31,37\n"),
            Map("filename" -> "primes.csv")),
          Multipart.FormData.BodyPart.Strict(
            "comment",
            HttpEntity("comment"))
        )

      Post("/broadcast", multipartForm) ~> route ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }
}
