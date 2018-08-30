package com.seanmcapp.util.parser

import com.seanmcapp.repository.{Customer, Vote, Photo}
import spray.json._

case class BroadcastMessage(recipient: Long, message: String, key: String)

object WebAPIJson extends DefaultJsonProtocol {

  implicit val broadcastMessageFormat = jsonFormat3(BroadcastMessage)

  implicit val customerFormat = jsonFormat3(Customer)

  implicit val voteFormat = jsonFormat3(Vote)

  implicit val photoFormat = jsonFormat5(Photo)

}