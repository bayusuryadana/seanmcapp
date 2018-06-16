package com.seanmcapp.util.parser

import spray.json._

case class InstagramNode(id: String, caption: String, thumbnailSrc: String, date: Long)
case class InstagramUser(id: String, biography: String, fullName: String, isPrivate: Boolean, username: String, nodes: Seq[InstagramNode])

object InstagramJson extends DefaultJsonProtocol {

  implicit val instagramNodeFormat = jsonFormat(InstagramNode, "id", "caption", "thumbnail_src", "date")
  implicit val instagamUserFormat = jsonFormat(InstagramUser, "id", "biography", "full_name", "is_private", "username", "nodes")

}
