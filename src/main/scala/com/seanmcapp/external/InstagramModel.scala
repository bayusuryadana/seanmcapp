package com.seanmcapp.external


////////// ad-hoc
case class InstagramCsrfToken(csrf_token: String)

// case class InstagramAccountResponse(logging_page_id: String, graphql: InstagramData)

case class InstagramRequestParameter(id: String, first: Int, after: Option[String])

////////// Instagram Post
case class InstagramResponse(data: InstagramData)
case class InstagramData(user: InstagramUser)
case class InstagramUser(edge_owner_to_timeline_media: InstagramMedia)
case class InstagramMedia(count: Int, page_info: InstagramPageInfo, edges: Seq[InstagramEdge])
case class InstagramPageInfo(has_next_page: Boolean, end_cursor: Option[String])
case class InstagramEdge(node: InstagramNode)
case class InstagramNode(id: String,
                         thumbnail_src: String,
                         taken_at_timestamp: Long,
                         edge_media_to_caption: InstagramEdgeMediaCaption,
                         display_url: String,
                         is_video: Boolean,
                         video_url: Option[String],
                         edge_sidecar_to_children: Option[InstagramEdgeSidecarChildren])
case class InstagramEdgeMediaCaption(edges: Seq[InstagramNodeCaption])
case class InstagramNodeCaption(node: InstagramCaption)
case class InstagramCaption(text: String)
case class InstagramEdgeSidecarChildren(edges: Seq[InstagramNodeChildren])
case class InstagramNodeChildren(node: InstagramChildren)
case class InstagramChildren(id: String, display_url: String, is_video: Boolean, video_url: Option[String])

////////// Instagram Story
case class InstagramStoryResponse(data: InstagramStoryData)
case class InstagramStoryData(reels_media: Seq[InstagramStoryReel])
case class InstagramStoryReel(items: Seq[InstagramStoryItem])
case class InstagramStoryItem(id: String, __typename: String, display_url: String, video_resources: Option[Seq[InstagramStoryVideoResource]])
case class InstagramStoryVideoResource(src: String, profile: String)
