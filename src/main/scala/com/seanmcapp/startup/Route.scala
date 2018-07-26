package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.api.{TelegramAPI, WebAPI}
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with SprayJsonSupport with Injection {

  private val webApi = new WebAPI(photoRepo, customerRepo)
  private val telegramApi = new TelegramAPI(customerRepo, photoRepo, voteRepo)
  private val instagramFetcher = new InstagramFetcher(customerRepo, photoRepo)

  val routePath = Seq(
    // fetcher
    get(path("sync")(complete(instagramFetcher.flow))), //instagram

    // API
    get(path("api" / Remaining)(method => complete(webApi.flow(JsString(method))))),
    post((path("api" / Remaining) & entity(as[JsValue]))((request, input) => complete(webApi.flow(JsString(request), input)))),
    post((path("webhook") & entity(as[JsValue]))(request => complete(telegramApi.flow(request)))), // telegram

    // util
    get(path("")(complete("selamat datang tot"))), // homepage
     // broadcast
  ).reduce{ (a,b) => a~b }

}
