package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, InstagramNode, InstagramStoryResponse, InstagramStoryVideoResource, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.instagram.AccountRepo
import com.seanmcapp.repository.{Cache, CacheRepo}
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class InstagramPost(id: String, caption: String, media: Seq[InstagramPostChild])
case class InstagramPostChild(isVideo: Boolean, sourceURL: String)

class StalkerService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo, accountRepo: AccountRepo) extends ScheduledTask {
  
  val chatId = -1001359004262L
  val privateId = 1

  override def run(): Future[Seq[TelegramResponse]] = fetch()

  def fetch(sessionIdOpt: Option[String] = None): Future[Seq[TelegramResponse]] = {
    val sessionId = sessionIdOpt.getOrElse(instagramClient.postLogin())
    val cacheF = cacheRepo.getAll()
    val accountsF = accountRepo.getAll()

    val resultF = for {
      accounts <- accountsF
    } yield {
      val accountsResponses = accounts.map { account =>
        val storiesF = Future(instagramClient.getStories(account.id, sessionId))
        val postsF = Future(instagramClient.getAllPosts(account.id, None, sessionId))
        for {
          stories <- storiesF
          posts <- postsF
          cache <- cacheF
        } yield {
          val storyCache = cache.filter(_.key.contains(s"instastory-")).map(_.key).toSet
          val postCache = cache.find(_.key.contains(s"instapost-${account.id}")).map(_.value.split(",").toSet).getOrElse(Set.empty[String])

          val storiesResult = processStory(storyCache, stories, account.alias)
          val postsResult = processPost(postCache, posts, account.id, account.alias)

          storiesResult ++ postsResult
        }
      }
      Future.sequence(accountsResponses).map(_.flatten)
    }
    
    resultF.flatten
  }
  
  private[service] def processStory(storyCache: Set[String], stories: InstagramStoryResponse, name: String): Seq[TelegramResponse] = {
    stories.data.reels_media.flatMap(_.items.flatMap { story =>
      val idKey = s"instastory-${story.id}"
      story.__typename match {
        case "GraphStoryImage" =>
          val imgUrl = story.display_url
          storyCache.find(_ == idKey) match {
            case Some(_) => None
            case _ =>
              cacheRepo.set(createStoryCache(idKey, imgUrl))
              val tRes = telegramClient.sendPhotoWithFileUpload(chatId, s"STORY - $name", telegramClient.getDataByteFromUrl(imgUrl))
              Some(tRes)
          }
        case "GraphStoryVideo" =>
          val videos = story.video_resources.getOrElse(Seq.empty[InstagramStoryVideoResource])
          val videoUrl = videos.find(_.profile == "MAIN").orElse(videos.headOption).getOrElse(throw new Exception("Video not found")).src
          storyCache.find(_ == idKey) match {
            case Some(_) => None
            case _ => 
              cacheRepo.set(createStoryCache(idKey, videoUrl))
              val tRes = telegramClient.sendVideoWithFileUpload(chatId, s"STORY - $name", telegramClient.getDataByteFromUrl(videoUrl))
              Some(tRes)
          }
        case _ => 
          println("Exception - Cannot process story for this type")
          None
      }
    })
  }
  
  private def createStoryCache(idKey: String, url: String): Cache =
    Cache(idKey, url, Some(new DateTime().plusHours(24).getMillis / 1000))
    
  private[service] def processPost(postCache: Set[String], posts: Seq[InstagramNode], id: String, name: String): Seq[TelegramResponse] = {
    val newCacheString = (postCache ++ posts.map(_.id).toSet).foldLeft("")((res, s) => s"$res,$s")
    val cacheToUpdate = Cache(s"instapost-$id", newCacheString, None)
    cacheRepo.set(cacheToUpdate)

    posts.map(convert).filterNot(p => postCache.contains(p.id)).flatMap { post =>
      val allMedia = post.media.map { media =>
        if (media.isVideo) 
          telegramClient.sendVideoWithFileUpload(chatId, data = telegramClient.getDataByteFromUrl(media.sourceURL)) 
        else
          telegramClient.sendPhotoWithFileUpload(chatId, data = telegramClient.getDataByteFromUrl(media.sourceURL))
      }
      telegramClient.sendMessage(chatId, s"POST - $name\n\n${post.caption}")
      allMedia
    }
  }

  private def convert(node: InstagramNode): InstagramPost = {
    val id = node.id
    val caption = node.edge_media_to_caption.edges.headOption.map(_.node.text).getOrElse("")
    val media: Seq[InstagramPostChild] = node.edge_sidecar_to_children match {
      case Some(i) => i.edges.map { e =>
        if (e.node.is_video) InstagramPostChild(true, e.node.video_url.getOrElse(throw new Exception("is_video but video_url not found")))
        else InstagramPostChild(false, e.node.display_url)
      }
      case _ =>
        val ipc = if (node.is_video) InstagramPostChild(true, node.video_url.getOrElse(throw new Exception("is_video but video_url not found")))
        else InstagramPostChild(false, node.display_url)
        Seq(ipc)
    }
    InstagramPost(id, caption, media)
  }
  
}
