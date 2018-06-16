package com.seanmcapp.startup

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.util.JsonProtocol
import com.seanmcapp.model.{BroadcastMessage, TelegramUpdate}
import com.seanmcapp.repository.PhotoRepo
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
            //InstagramService.flow.map(_.toJson)
            ""
          }
        } ~
      path("latest") {
        complete {
          PhotoRepo.getLatest.map(_.toJson)
        }
      } ~
      path("random") {
        complete {
          PhotoRepo.getRandom.map(_.toJson)
        }
      }
    } ~
    post {
      (path("webhook") & entity(as[TelegramUpdate])) { request =>
        complete {
          //TelegramService.flowWebhook(request)
          200 -> "No Throwable supposed to be mean succeed".toJson
        }
      } ~
      (path("broadcast") & entity(as[BroadcastMessage])) { request =>
        complete {
          //val result = TelegramService.flowBroadcast(request)
          //result._1 -> result._2
          ""
        }
      }
    }
  }
}
