package com.seanmcapp.service

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

}
