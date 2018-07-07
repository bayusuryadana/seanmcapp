package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.fetcher.InstagramFetcher
import com.seanmcapp.api.{BroadcastAPI, TelegramAPI, WebAPI}
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with SprayJsonSupport with Injection {

  private val webApi = new WebAPI(photoRepo)
  private val telegramApi = new TelegramAPI(customerRepo, photoRepo, voteRepo)
  private val broadcastApi = new BroadcastAPI(customerRepo)
  private val instagramFetcher = new InstagramFetcher(customerRepo, photoRepo)

  val routePath = Seq(
    // fetcher
    get(path("sync")(complete(instagramFetcher.flow))), //instagram

    // API
    get(path("api")(parameters('m){(method) => complete(webApi.flow(method))})), // web
    post((path("webhook") & entity(as[JsValue]))(request => complete(telegramApi.flow(request)))), // telegram

    // util
    get(path("")(complete("selamat datang tot"))), // homepage
    post((path("broadcast") & entity(as[JsValue]))(request => complete(broadcastApi.flow(request)))) // broadcast
  ).reduce{ (a,b) => a~b }

}
