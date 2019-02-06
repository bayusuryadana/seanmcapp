package com.seanmcapp.util.parser

import com.seanmcapp.repository.instagram.{Customer, Photo, Vote}
import spray.json._

case class BroadcastMessage(recipient: Long, message: String, key: String)

case class Result(name: String, count: Long, avg: Double, account: String = "")

object WebAPIJson extends DefaultJsonProtocol {

  implicit val broadcastMessageFormat = jsonFormat3(BroadcastMessage)

  implicit val customerFormat = jsonFormat3(Customer)

  implicit val voteFormat = jsonFormat(Vote, "photos_id", "customers_id", "rating")

  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

  implicit val resultFormat = jsonFormat4(Result)

}