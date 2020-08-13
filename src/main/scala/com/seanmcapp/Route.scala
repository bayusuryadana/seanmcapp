package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.encoder.{RouteEncoder, TelegramResponse}
import scala.concurrent.duration._
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives
  with SprayJsonSupport with RouteEncoder with Injection {

  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

  val routePath = List(

    // cbc API
    get(path("cbc" / "random")(complete(cbcAPI.random.map(_.map(_.toJson))))),
    post((path("cbc" / "webhook") & entity(as[JsValue]))
      (payload => complete(cbcAPI.randomFlow(payload).map(res => encode[Option[TelegramResponse]](res))))),

    // instagram fetcher API
    get(path("instagram" / Remaining)(cookie => complete(instagramFetcher.fetch(cookie).map(_.toJson)))),

    // dota APP
    get(path("dota")(complete(dotaAPI.home.map(_.toJson)))),

    // wallet
    get {
      (path("wallet") & headerValue(getHeader("secretkey"))) { secretKey =>
        complete(walletAPI.getAll(secretKey).map(_.toJson))
      }
    },
    post {
      (path("wallet") & headerValue(getHeader("secretkey")) & entity(as[JsValue])) { (secretKey, payload) =>
        complete(walletAPI.insert(payload)(secretKey).map(_.toJson))
      }
    },
    put {
      (path("wallet") & headerValue(getHeader("secretkey")) & entity(as[JsValue])) { (secretKey, payload) =>
        complete(walletAPI.update(payload)(secretKey).map(_.toJson))
      }
    },
    delete {
      (path("wallet" / Remaining) & headerValue(getHeader("secretkey"))) { (id, secretKey) =>
        complete(walletAPI.delete(id.toInt)(secretKey).map(_.toJson))
      }
    },

    // broadcast
    toStrictEntity(3.seconds) {
      post((path("broadcast") & headerValue(getHeader("secretkey")) & fileUpload("photo") & formFieldMap) {
        case (secretKey, (_, byteSource), formFields) =>
          complete(broadcastAPI.broadcastWithPhoto(byteSource, formFields)(mat, secretKey))
      })
    },

    // amartha
    get {
      (path("amartha") & headerValue(getHeader("username")) & headerValue(getHeader("password"))) { (username, password) =>
        complete(amarthaAPI.getAmarthaResult(username, password))
      }
    },

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !")))

  ).reduce{ (a,b) => a~b }

  private def getHeader(key: String): HttpHeader => Option[String] = {
    (httpHeader: HttpHeader) => {
      HttpHeader.unapply(httpHeader) match {
        case Some((k, v)) if k == key => Some(v)
        case _ => None
      }
    }
  }

}
