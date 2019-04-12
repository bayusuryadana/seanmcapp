package com.seanmcapp.util.parser

import com.seanmcapp.repository.instagram.{Customer, Photo, Vote}
import spray.json._

object WebAPIJson extends DefaultJsonProtocol {

  implicit val customerFormat = jsonFormat3(Customer)

  implicit val voteFormat = jsonFormat(Vote, "photos_id", "customers_id", "rating")

  implicit val photoFormat = jsonFormat(Photo, "id", "thumbnail_src", "date", "caption", "account")

}