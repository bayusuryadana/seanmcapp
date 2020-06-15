package com.seanmcapp.util.parser

import spray.json._

case class BroadcastOutput(success: Boolean)

trait BroadcasterCommon extends DefaultJsonProtocol {
  implicit val broadcastOutputFormat = jsonFormat(BroadcastOutput, "success")
}
