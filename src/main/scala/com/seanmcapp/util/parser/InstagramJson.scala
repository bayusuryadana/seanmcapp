package com.seanmcapp.util.parser

import spray.json._

case class InstagramUpdate(data: InstagramData)
case class InstagramData(user: InstagramUser)
case class InstagramUser(edgeToOwnerMedia: InstagramKontol)
case class InstagramKontol(edges: Seq[InstagramEdge])
case class InstagramEdge(node: InstagramNode)
case class InstagramNode(id: String, caption: InstagramKontolCaption, thumbnailSrc: String, date: Long)
case class InstagramKontolCaption(edges: Seq[InstagramNodeCaption])
case class InstagramNodeCaption(node: InstagramCaption)
case class InstagramCaption(text: String)

case class InstagramNodeResult(id: String, caption: String, thumbnailSrc: String, date: Long)


object InstagramJson extends DefaultJsonProtocol {

  implicit val instagramNodeResult = jsonFormat(InstagramNodeResult, "id", "caption", "thumbnail_src", "date")


  implicit val instagramCaption = jsonFormat(InstagramCaption, "text")

  implicit val instagramNodeCaptionFormat = jsonFormat(InstagramNodeCaption, "node")

  implicit val instagramKontolCaptionFormat = jsonFormat(InstagramKontolCaption, "edges")

  implicit val instagramNodeFormat = jsonFormat(InstagramNode, "id", "edge_media_to_caption", "thumbnail_src", "taken_at_timestamp")

  implicit val instagramEdge = jsonFormat(InstagramEdge, "node")

  implicit val instagramKontolFormat = jsonFormat(InstagramKontol, "edges")

  implicit val instagamUserFormat = jsonFormat(InstagramUser, "edge_owner_to_timeline_media")

  implicit val instagramDataFormat = jsonFormat(InstagramData, "user")

  implicit val instagramUpdateFormat = jsonFormat(InstagramUpdate, "data")

}
