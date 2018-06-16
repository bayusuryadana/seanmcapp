package com.seanmcapp.util.parser

import spray.json._

case class BroadcastMessage(recipient: Long, message: String, key: String)

object BroadcastJson extends DefaultJsonProtocol {
  implicit val broadcastMessageFormat = jsonFormat(BroadcastMessage, "recipient", "message", "key")
}