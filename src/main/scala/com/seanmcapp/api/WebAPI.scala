package com.seanmcapp.api

import com.seanmcapp.repository.{Photo, PhotoRepo}
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object WebAPI extends API {

  implicit val photoFormat = jsonFormat5(Photo)

  def flow(request: String): Future[JsValue] = {
    request match {
      case "latest" => PhotoRepo.getLatest.map(_.toJson)
      case "random" => PhotoRepo.getRandom.map(_.toJson)
      case _ => Future.successful(JsString("no such method"))
    }
  }

}
