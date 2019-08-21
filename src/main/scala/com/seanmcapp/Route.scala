package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.encoder.{RouteEncoder, TelegramResponse}
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives
  with SprayJsonSupport with RouteEncoder with Injection {

  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

  val routePath = Seq(

    // cbc API
    get(path("cbc" / "random")(complete(cbcAPI.random.map(_.map(_.toJson))))),
    post((path("cbc" / "webhook") & entity(as[JsValue]))
      (request => complete(cbcAPI.randomFlow(request).map(res => encode[Option[TelegramResponse]](res))))),

    // instagram fetcher API
    get(path("instagram" / Remaining)(cookie => complete(instagramFetcher.fetch(cookie).map(_.toJson)))),

    // dota APP
    get(path("dota")(complete(dotaAPI.home.map(_.toJson)))),
    get(path("dota" / "player" / Remaining)(id => complete(dotaAPI.player(id.toInt).map(_.toJson)))),
    get(path("dota" / "hero" /  Remaining)(id => complete(dotaAPI.hero(id.toInt).map(_.toJson)))),

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !"))),

  ).reduce{ (a,b) => a~b }

}
