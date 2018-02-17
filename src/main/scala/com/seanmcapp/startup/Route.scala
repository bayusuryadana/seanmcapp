package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.helper.JsonProtocol
import com.seanmcapp.model.{BroadcastMessage, TelegramMessage}
import com.seanmcapp.repository.PhotoRepo
import com.seanmcapp.service.{InstagramService, TelegramService}
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives with JsonProtocol {

  val routePath = {
    get {
      path("") {
        complete("selamat datang tot")
      } ~
        path("sync") {
          complete {
            InstagramService.flow("ui.cantik").map(_.toJson)
          }
        } ~
      path("getLatest") {
        complete {
          PhotoRepo.getLatest.map(_.toJson)
        }
      } ~
      path("getRandom") {
        complete {
          PhotoRepo.getRandom.map(_.toJson)
        }
      }
    } ~
    post {
      (path("webhook") & entity(as[TelegramMessage])) { request =>
        complete {
          TelegramService.flow(request)
          200 -> "No Throwable supposed to be mean succeed"
        }
      } ~
      (path("broadcast") & entity(as[BroadcastMessage])) { request =>
        complete {
          val result = TelegramService.flowBroadcast(request)
          result._1 -> result._2
        }
      }
    }
  }
}
