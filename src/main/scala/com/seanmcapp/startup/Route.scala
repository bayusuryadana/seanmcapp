package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with SprayJsonSupport with Injection {

  val routePath = Seq(

    // cbc API
    get(path("api" / Remaining)(method => complete(webAPI.get(JsString(method))))), // web API
    post((path("webhook") & entity(as[JsValue]))(request => complete(telegramAPI.flow(request)))), // telegram

    // birthday API
    get(path("birthday")(complete(birthdayAPI.check))),

    // dota APP
    get(path("dota")(complete(dotaAPP.home))),
    get(path("dota" / "player" / Remaining)(id => complete(dotaAPP.player(id.toInt)))),
    get(path("dota" / "hero" /  Remaining)(id => complete(dotaAPP.hero(id.toInt)))),

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !"))),

  ).reduce{ (a,b) => a~b }

}
