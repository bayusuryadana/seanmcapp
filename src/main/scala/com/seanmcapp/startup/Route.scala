package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with SprayJsonSupport with Injection {

  val routePath = Seq(

    // API
    get(path("api" / Remaining)(method => complete(webAPI.get(JsString(method))))), // web API
    post((path("webhook") & entity(as[JsValue]))(request => complete(telegramAPI.flow(request)))), // telegram

    // birthday check
    get(path("birthday")(complete(birthdayAPI.check))),

    // dota
    get(path("dota")(complete(dotaAPI.getRecentMatches))),
    get(path("dota" / "player")(complete(dotaAPI.getPlayers))),
    get(path("dota" / "player" / Remaining)(id => complete(dotaAPI.getPlayerMatches(id.toInt)))),
    get(path("dota" / "hero")(complete(dotaAPI.getHeroes))),
    get(path("dota" / "hero" /  Remaining)(id => complete(dotaAPI.getHeroMatches(id.toInt)))),

    // Statistics
    get(path("stats")(complete(webAPI.stats()))),

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !"))),

  ).reduce{ (a,b) => a~b }

}
