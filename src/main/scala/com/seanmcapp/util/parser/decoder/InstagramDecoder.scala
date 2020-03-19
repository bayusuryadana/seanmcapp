package com.seanmcapp.util.parser.decoder

case class InstagramAccountResponse(id: String)

case class InstagramResponse(graphql: InstagramData)
case class InstagramData(user: InstagramUser)
case class InstagramUser(media: InstagramMedia)
case class InstagramMedia(count: Int, pageInfo: InstagramPageInfo, edges: Seq[InstagramEdge])
case class InstagramPageInfo(hasNextPage: Boolean, endCursor: Option[String])
case class InstagramEdge(node: InstagramNode)
case class InstagramNode(id: String, thumbnailSrc: String, date: Long, caption: InstagramMediaCaption)
case class InstagramMediaCaption(edges: Seq[InstagramEdgeCaption])
case class InstagramEdgeCaption(node: InstagramCaption)
case class InstagramCaption(text: String)

trait InstagramDecoder extends JsonDecoder {
  implicit val instagramAccountResponseFormat = jsonFormat(InstagramAccountResponse, "logging_page_id")

  implicit val instagramCaptionFormat = jsonFormat(InstagramCaption, "text")
  implicit val instagramEdgeCaptionFormat = jsonFormat(InstagramEdgeCaption, "node")
  implicit val instagramMediaCaptionFormat = jsonFormat(InstagramMediaCaption, "edges")
  implicit val instagramNodeFormat = jsonFormat(InstagramNode, "id", "thumbnail_src", "taken_at_timestamp", "edge_media_to_caption")
  implicit val instagramEdgeNodeFormat = jsonFormat(InstagramEdge, "node")
  implicit val instagramPageInfoFormat = jsonFormat(InstagramPageInfo, "has_next_page", "end_cursor")
  implicit val instagramMediaFormat = jsonFormat(InstagramMedia, "count", "page_info", "edges")
  implicit val instagramUserFormat = jsonFormat(InstagramUser, "edge_owner_to_timeline_media")
  implicit val instagramDataFormat = jsonFormat(InstagramData, "user")
  implicit val instagramResponseFormat = jsonFormat(InstagramResponse, "graphql")

}