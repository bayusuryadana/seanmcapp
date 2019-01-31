package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with SprayJsonSupport with Injection {

  val routePath = Seq(
    // fetcher
    //get(path("sync")(complete(instagramFetcher.flow))), //instagram

    // API
    get(path("api" / Remaining)(method => complete(webAPI.get(JsString(method))))), // web API
    post((path("webhook") & entity(as[JsValue]))(request => complete(telegramAPI.flow(request)))), // telegram

    // Statistics
    get(path("stats")(complete(webAPI.stats()))),

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !"))),

  ).reduce{ (a,b) => a~b }

}
