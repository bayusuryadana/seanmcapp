package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.api.{TelegramAPI, WebAPI}
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with SprayJsonSupport {

  val routePath = Seq(
    // fetcher
    get(path("sync")(complete(InstagramFetcher.flow))), //instagram

    // API
    get(path("api")(parameters('m){(method) => complete(WebAPI.flow(method))})), // web
    post((path("webhook") & entity(as[JsValue]))(request => complete(TelegramAPI.flow(request)))), // telegram

    // util
    get(path("")(complete("selamat datang tot"))), // homepage
    post((path("broadcast") & entity(as[JsValue]))(request => complete(TelegramAPI.flowBroadcast(request)))) // broadcast
  ).reduce{ (a,b) => a~b }

}
