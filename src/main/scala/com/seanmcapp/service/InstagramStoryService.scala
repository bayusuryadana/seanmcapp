package com.seanmcapp.service

import java.net.URL
import java.util.concurrent.TimeUnit

import com.seanmcapp.external.{InstagramClient, TelegramClient}
import com.seanmcapp.repository.RedisRepo
import com.seanmcapp.util.MemoryCache
import scalacache.modes.sync._

import scala.concurrent.duration.FiniteDuration

case class InstagramStoryRequestParameter(reel_ids: Seq[String], precomposed_overlay: Boolean)

case class InstagramStoryResponse(data: InstagramStoryData)
case class InstagramStoryData(reels_media: Seq[InstagramStoryReel])
case class InstagramStoryReel(items: Seq[InstagramStoryItem])
case class InstagramStoryItem(id: String, __typename: String, display_url: String, video_resources: Option[Seq[InstagramStoryVideoResource]])
case class InstagramStoryVideoResource(src: String, profile: String)


class InstagramStoryService(instagramClient: InstagramClient, telegramClient: TelegramClient, redisRepo: RedisRepo) extends ScheduledTask {

  override def run(): Seq[String] = {
    val sessionId = instagramClient.postLogin()
    //val userId = instagramClient.getAccountResponse("").logging_page_id.replace("profilePage_", "")
    val accounts = List("277395688", "302844663")
    val storyResults = accounts.map(id => instagramClient.getStories(id, sessionId))
    storyResults.flatMap { story =>
      story.data.reels_media.flatMap(_.items.map { i =>
        val chatId = -1001359004262L
        val idKey = s"instastory-${i.id}"
        i.__typename match {
          case "GraphStoryImage" =>
            val imgUrl = i.display_url
            if (redisRepo.get(idKey).isEmpty) {
              telegramClient.sendPhotoWithFileUpload(chatId, data = getDataByte(imgUrl))
              redisRepo.set(idKey, imgUrl, Some(FiniteDuration(24, TimeUnit.HOURS)))
            }
            imgUrl
          case "GraphStoryVideo" =>
            val videos = i.video_resources.getOrElse(Seq.empty[InstagramStoryVideoResource])
            val videoUrl = videos.find(_.profile == "MAIN").orElse(videos.headOption).getOrElse(throw new Exception("Video not found")).src
            if (redisRepo.get(idKey).isEmpty) {
              telegramClient.sendVideoWithFileUpload(chatId, data = getDataByte(videoUrl))
              redisRepo.set(idKey, videoUrl, Some(FiniteDuration(24, TimeUnit.HOURS)))
            }
            videoUrl
        }
      })
    }
  }

  // $COVERAGE-OFF$
  private[service] def getDataByte(url: String): Array[Byte] = {
    val inputStream = new URL(url).openStream
    LazyList.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
  }
  // $COVERAGE-ON$
}
