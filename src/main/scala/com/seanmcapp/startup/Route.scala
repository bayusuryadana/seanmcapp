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
    get(path("sync")(complete(instagramFetcher.flow))), //instagram

    // API
    get(path("api" / Remaining)(method => complete(webAPI.flow(JsString(method))))),
    post((path("api" / Remaining) & entity(as[JsValue]))((request, input) => complete(webAPI.flow(JsString(request), input)))),
    post((path("webhook") & entity(as[JsValue]))(request => complete(telegramAPI.flow(request)))), // telegram

    // util
    get(path("")(complete("selamat datang tot"))), // homepage
     // broadcast
  ).reduce{ (a,b) => a~b }

}
