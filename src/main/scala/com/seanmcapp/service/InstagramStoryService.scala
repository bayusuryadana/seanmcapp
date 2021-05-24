package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient}
import com.seanmcapp.repository.{Cache, CacheRepo}
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class InstagramStoryRequestParameter(reel_ids: Seq[String], precomposed_overlay: Boolean)

case class InstagramStoryResponse(data: InstagramStoryData)
case class InstagramStoryData(reels_media: Seq[InstagramStoryReel])
case class InstagramStoryReel(items: Seq[InstagramStoryItem])
case class InstagramStoryItem(id: String, __typename: String, display_url: String, video_resources: Option[Seq[InstagramStoryVideoResource]])
case class InstagramStoryVideoResource(src: String, profile: String)


class InstagramStoryService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo) extends ScheduledTask {

  override def run(): Future[Seq[String]] = fetch()

  def fetch(sessionIdOpt: Option[String] = None): Future[Seq[String]] = {
    val sessionId = sessionIdOpt.getOrElse(instagramClient.postLogin())
    val accountMap = Map(
      "Alvida" -> "302844663",
      "Buggy" -> "277395688",
      "Gecko Moria" -> "5646204159"
    )
    val resultF = accountMap.toList.flatMap { case (name, id) =>
      val story = instagramClient.getStories(id, sessionId)
      story.data.reels_media.flatMap(_.items.map { i =>
        val chatId = -1001359004262L
        val idKey = s"instastory-${i.id}"
        i.__typename match {
          case "GraphStoryImage" =>
            val imgUrl = i.display_url
            cacheRepo.get(idKey).map{ valOpt =>
              if (valOpt.isEmpty) {
                telegramClient.sendPhotoWithFileUpload(chatId, name, imgUrl)
                val cache = Cache(idKey, imgUrl, Some(new DateTime().plusHours(24).getMillis / 1000))
                cacheRepo.set(cache)
              }
              imgUrl
            }
          case "GraphStoryVideo" =>
            val videos = i.video_resources.getOrElse(Seq.empty[InstagramStoryVideoResource])
            val videoUrl = videos.find(_.profile == "MAIN").orElse(videos.headOption).getOrElse(throw new Exception("Video not found")).src
            cacheRepo.get(idKey).map { valOpt =>
              if (valOpt.isEmpty) {
                telegramClient.sendVideoWithFileUpload(chatId, name, videoUrl)
                val cache = Cache(idKey, videoUrl, Some(new DateTime().plusHours(24).getMillis / 1000))
                cacheRepo.set(cache)
              }
              videoUrl
            }
        }
      })
    }

    Future.sequence(resultF)
  }
}
