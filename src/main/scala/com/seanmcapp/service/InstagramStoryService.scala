package com.seanmcapp.service

import java.net.URL
import java.util.concurrent.TimeUnit

import com.seanmcapp.external.{InstagramClient, TelegramClient}
import com.seanmcapp.repository.RedisRepo

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
    val accountMap = Map(
      "Alvida" -> "302844663",
      "Buggy" -> "277395688",
      "Gecko Moria" -> "5646204159"
    )
    accountMap.toList.flatMap { case (name, id) =>
      val story = instagramClient.getStories(id, sessionId)
      story.data.reels_media.flatMap(_.items.map { i =>
        val chatId = -1001359004262L
        val idKey = s"instastory-${i.id}"
        i.__typename match {
          case "GraphStoryImage" =>
            val imgUrl = i.display_url
            if (redisRepo.get(idKey).isEmpty) {
              telegramClient.sendPhotoWithFileUpload(chatId, name, getDataByte(imgUrl))
              redisRepo.set(idKey, imgUrl, Some(FiniteDuration(24, TimeUnit.HOURS)))
            }
            imgUrl
          case "GraphStoryVideo" =>
            val videos = i.video_resources.getOrElse(Seq.empty[InstagramStoryVideoResource])
            val videoUrl = videos.find(_.profile == "MAIN").orElse(videos.headOption).getOrElse(throw new Exception("Video not found")).src
            if (redisRepo.get(idKey).isEmpty) {
              telegramClient.sendVideoWithFileUpload(chatId, name, getDataByte(videoUrl))
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
