package com.seanmcapp.service

import java.net.URL
import java.util.concurrent.TimeUnit

import com.seanmcapp.external.{InstagramClient, TelegramClient}
import com.seanmcapp.util.MemoryCache
import scalacache.modes.sync._

import scala.concurrent.duration.FiniteDuration

case class InstagramStoryRequestParameter(reel_ids: Seq[String], precomposed_overlay: Boolean)

case class InstagramStoryResponse(data: InstagramStoryData)
case class InstagramStoryData(reels_media: Seq[InstagramStoryReel])
case class InstagramStoryReel(items: Seq[InstagramStoryItem])
case class InstagramStoryItem(id: String, __typename: String, display_url: String, video_resources: Option[Seq[InstagramStoryVideoResource]])
case class InstagramStoryVideoResource(src: String, profile: String)


class InstagramStoryService(instagramClient: InstagramClient, telegramClient: TelegramClient) extends MemoryCache with ScheduledTask {

  implicit val storiesCache = createCache[String]

  override def run(): Seq[String] = {
    val sessionId = instagramClient.postLogin()
    //val userId = instagramClient.getAccountResponse("").logging_page_id.replace("profilePage_", "")
    val stories = instagramClient.getStories("277395688", sessionId)
    stories.data.reels_media.flatMap(_.items.map { i =>
      val chatId = -1001359004262L
      i.__typename match {
        case "GraphStoryImage" =>
          val imgUrl = i.display_url
          if (storiesCache.get(i.id).isEmpty) {
            val inputStream = new URL(imgUrl).openStream
            val data  = LazyList.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
            telegramClient.sendPhotoWithFileUpload(chatId, data = data)
            storiesCache.put(i.id)(imgUrl, Some(FiniteDuration(24, TimeUnit.HOURS)))
          }
          imgUrl
        case "GraphStoryVideo" =>
          val videos = i.video_resources.getOrElse(Seq.empty[InstagramStoryVideoResource])
          val videoUrl = videos.find(_.profile == "MAIN").orElse(videos.headOption).getOrElse(throw new Exception("Video not found")).src
          if (storiesCache.get(i.id).isEmpty) {
            val inputStream = new URL(videoUrl).openStream
            val data  = LazyList.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
            telegramClient.sendVideoWithFileUpload(chatId, data = data)
            storiesCache.put(i.id)(videoUrl, Some(FiniteDuration(24, TimeUnit.HOURS)))
          }
          videoUrl
      }
    })
  }
}
