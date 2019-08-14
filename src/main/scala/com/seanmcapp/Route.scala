package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.TelegramUpdate
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives
  with SprayJsonSupport with DefaultJsonProtocol with Injection {

  import com.seanmcapp.util.parser.TelegramJson._
  import com.seanmcapp.util.parser.DotaOutputJson._
  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

  val routePath = Seq(

    // cbc API
    get(path("cbc" / "random")(complete(cbcAPI.random.map(_.map(_.toJson))))),
    post((path("cbc" / "webhook") & entity(as[JsValue])) { request =>
      val telegramRequest = Try(request.convertTo[TelegramUpdate]).toOption

      val responseF = telegramRequest match {
        case Some(r) if r.message.isDefined =>
          cbcAPI.randomFlow(r.message.get)
        case _ =>
          println("[ERROR] cannot recognized payload type: " + request)
          Future.successful(None)
      }

      complete(responseF.map(_.toJson))
    }),

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
