package com.seanmcapp.util.parser.decoder

import spray.json._

import scala.util.{Failure, Success, Try}

trait JsonDecoder extends DefaultJsonProtocol {
  def decode[T:JsonReader](jsonString: String): T = {
    Try(jsonString.parseJson.convertTo[T]) match {
      case Success(res) => res
      case Failure(e) =>
        println(s"RESPONSE: $jsonString")
        throw new Exception(e)
    }
  }

  def decode[T:JsonReader](jsValue: JsValue): T = {
    Try(jsValue.convertTo[T]) match {
      case Success(res) => res
      case Failure(e) =>
        println(s"RESPONSE: ${jsValue.prettyPrint}")
        throw new Exception(e)
    }
  }
}
