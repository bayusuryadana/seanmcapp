package com.seanmcapp.util.parser.encoder

import spray.json._

trait Encoder extends DefaultJsonProtocol {
  def encode[T](in: T)(implicit fmt: JsonWriter[T]): JsValue = in.toJson
}
