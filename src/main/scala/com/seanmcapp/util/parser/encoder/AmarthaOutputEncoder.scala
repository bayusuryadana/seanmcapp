package com.seanmcapp.util.parser.encoder

case class AmarthaAuthPayload(username: String, password: String)

trait AmarthaOutputEncoder extends Encoder {
  implicit val amarthaAuthPayloadFormat = jsonFormat2(AmarthaAuthPayload)
}
