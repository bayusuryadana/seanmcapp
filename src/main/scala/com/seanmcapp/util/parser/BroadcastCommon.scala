package com.seanmcapp.util.parser

import spray.json._

case class BroadcastOutput(success: Boolean, reason: Option[String])

trait BroadcasterCommon extends DefaultJsonProtocol {
  implicit val broadcastOutputFormat = jsonFormat2(BroadcastOutput)
}
