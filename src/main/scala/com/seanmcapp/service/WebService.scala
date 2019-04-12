package com.seanmcapp.service

import com.seanmcapp.repository.instagram.{Customer, Photo, Vote}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait WebService extends CBCService {

  import com.seanmcapp.util.parser.WebAPIJson._

  def get(request: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "latest" => photoRepo.getLatest.map(_.toJson)
      case "random" => photoRepo.getRandom(None).map(_.toJson)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  def post(request: JsValue, input: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "random" => randomFlow(input)
      case "vote" => voteFlow(input)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  private def randomFlow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (RANDOM) =====\n" + input + "\n")
    val request = input.convertTo[Customer]
    val customer = Customer(request.id, request.name, request.platform)
    getRandom(customer, None, None)((p:Photo) => p).map(_.toJson)
  }

  private def voteFlow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (VOTE) =====\n" + input + "\n")
    val request = input.convertTo[Vote]
    val vote = Vote(request.photoId, request.customerId, request.rating)
    doVote(vote).map(_ => 200.toJson)
  }

}
