package com.seanmcapp.util.parser.decoder

import spray.json._

import scala.util.{Failure, Success, Try}

trait Decoder extends DefaultJsonProtocol {
  def decode[T](jsonString: String)(implicit fmt: JsonReader[T]): T = {
    Try(jsonString.parseJson.convertTo[T]) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }

  def decode[T](jsValue: JsValue)(implicit fmt: JsonReader[T]): T = {
    Try(jsValue.convertTo[T]) match {
      case Success(res) => res
      case Failure(e) => throw new Exception(e)
    }
  }
}
