package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.Materializer
import com.seanmcapp.repository.instagram.{Customer, Vote}
import com.seanmcapp.util.parser.TelegramUpdate
import spray.json._

import scala.concurrent.ExecutionContext

class Route(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends Directives
  with SprayJsonSupport with DefaultJsonProtocol with Injection {

  import com.seanmcapp.util.parser.CBCJson._
  import com.seanmcapp.util.parser.DotaJson._

  val routePath = Seq(

    // cbc API
    post((path("cbc" / "random") & entity(as[JsValue])) { request =>
      println("/cbc/random: \n" + request + "\n")
      val customer = request.convertTo[Customer]
      complete(cbcAPI.random(customer).map(_.map(_.toJson)))
    }),
    post((path("cbc" / "vote") & entity(as[JsValue])) { request =>
      println("/cbc/vote: \n" + request + "\n")
      val vote = request.convertTo[Vote]
      complete(cbcAPI.vote(vote).map(_.toJson))
    }),
    post((path("cbc" / "webhook") & entity(as[JsValue])) { request =>
      val telegramRequest = request.convertTo[TelegramUpdate]

      val statusCodeF = telegramRequest match {
        case r if telegramRequest.message.isDefined =>
          println("/cbc/webhook(random): \n" + request + "\n")
          cbcAPI.randomFlow(r.message.get)
        case r if telegramRequest.callbackQuery.isDefined =>
          println("/cbc/webhook(vote): \n" + request + "\n")
          cbcAPI.voteFlow(r.callbackQuery.get)
        case _ => throw new Exception("cannot recognized payload type")
      }

      complete(statusCodeF.map(n => JsNumber(n)))
    }),

    // dota APP
    get(path("dota")(complete(dotaAPI.home.map(_.toJson)))),
    get(path("dota" / "player" / Remaining)(id => complete(dotaAPI.player(id.toInt).map(_.toJson)))),
    get(path("dota" / "hero" /  Remaining)(id => complete(dotaAPI.hero(id.toInt).map(_.toJson)))),

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !"))),

  ).reduce{ (a,b) => a~b }

}
