package com.seanmcapp.util.parser

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.seanmcapp.util.parser.decoder.JsonDecoder
import spray.json._

case class BroadcastOutput(success: Boolean)

trait BroadcasterCommon extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val broadcastOutputFormat = jsonFormat(BroadcastOutput, "success")
}
