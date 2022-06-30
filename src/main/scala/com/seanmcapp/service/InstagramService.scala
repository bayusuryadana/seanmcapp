package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, InstagramNode, InstagramStoryResponse, InstagramStoryVideoResource, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.instagram.{Account, AccountGroupType, AccountGroupTypes, AccountRepo}
import com.seanmcapp.repository.{Cache, CacheRepo, FeatureTypes}
import com.seanmcapp.util.ChatIdType
import org.joda.time.DateTime

import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class InstagramPost(id: String, caption: String, media: Seq[InstagramPostChild])
case class InstagramPostChild(isVideo: Boolean, sourceURL: String)

class InstagramService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo, accountRepo: AccountRepo) {
  
  def fetchPosts(fetchAccountType: AccountGroupType, chatIdType: ChatIdType, sessionIdOpt: Option[String] = None): Future[Seq[TelegramResponse]] = {
    val sessionId = if (fetchAccountType != AccountGroupTypes.StalkerSpecial)
      sessionIdOpt.getOrElse(instagramClient.postLogin())
    else ""
    val accountsF = accountRepo.getAll(fetchAccountType)

    val resultF = for {
      accounts <- accountsF
    } yield {
      val accountsResponses = accounts.map { account =>
        val postsF = Future(instagramClient.getAllPosts(account.id, None, sessionId, true))
        val postCacheF = cacheRepo.get(FeatureTypes.InstaPost.i, account.id)
        for {
          posts <- postsF
          postCache <- postCacheF
        } yield {
          processPost(chatIdType, postCache, posts, account.id, account)
        }
      }
      Future.sequence(accountsResponses).map(_.flatten)
    }
    resultF.flatten
  }
  
  def fetchStories(fetchAccountType: AccountGroupType, chatIdType: ChatIdType, sessionIdOpt: Option[String] = None): Future[Seq[TelegramResponse]] = {
    val sessionId = sessionIdOpt.getOrElse(instagramClient.postLogin())
    val accountsF = accountRepo.getAll(fetchAccountType)
    
    val resultF = for {
      accounts <- accountsF
    } yield {
      val accountsResponses = accounts.map { account =>
        val storiesF = Future(instagramClient.getStories(account.id, sessionId))
        val storyCacheF = cacheRepo.get(FeatureTypes.InstaStory.i, account.id)
        for {
          stories <- storiesF
          storyCache <- storyCacheF
        } yield {
          processStory(chatIdType, storyCache, stories, account)
        }
      }
      Future.sequence(accountsResponses).map(_.flatten)
    }

    resultF.flatten
  }

  private[service] def processPost(chatIdType: ChatIdType, postCache: Set[String], posts: Seq[InstagramNode], id: String, account: Account): Seq[TelegramResponse] = {
    val newPosts = posts.map(convert).filterNot(p => postCache.contains(p.id))
    val results = newPosts.flatMap { post =>
      val allMedia = post.media.map { media =>
        if (media.isVideo)
          telegramClient.sendVideoWithFileUpload(chatIdType.i, data = telegramClient.getDataByteFromUrl(media.sourceURL))
        else
          telegramClient.sendPhotoWithFileUpload(chatIdType.i, data = telegramClient.getDataByteFromUrl(media.sourceURL))
      }
      telegramClient.sendMessage(chatIdType.i, s"POST - ${account.alias}\n\n${post.caption}")
      allMedia
    }
    
    Await.result(
      cacheRepo.set(
        Cache(FeatureTypes.InstaPost.i, account.id, createCacheValue(postCache, newPosts.map(_.id).toSet), None)
      ), Duration(10, TimeUnit.SECONDS)
    )
    
    results
  }

  private[service] def processStory(chatIdType: ChatIdType, storyCache: Set[String], stories: InstagramStoryResponse, account: Account): Seq[TelegramResponse] = {
    val newStories = stories.data.reels_media.flatMap(_.items.filterNot(story => storyCache.contains(story.id)))
    val telegramResponses = newStories.flatMap { story =>
      story.__typename match {
        case "GraphStoryImage" =>
          val imgUrl = story.display_url
          val tRes = telegramClient.sendPhotoWithFileUpload(chatIdType.i, s"STORY - ${account.alias}", telegramClient.getDataByteFromUrl(imgUrl))
          Some(tRes)
        case "GraphStoryVideo" =>
          val videos = story.video_resources.getOrElse(Seq.empty[InstagramStoryVideoResource])
          val videoUrl = videos.find(_.profile == "MAIN").orElse(videos.headOption).getOrElse(throw new Exception("Video not found")).src
          val tRes = telegramClient.sendVideoWithFileUpload(chatIdType.i, s"STORY - ${account.alias}", telegramClient.getDataByteFromUrl(videoUrl))
          Some(tRes)
        case _ => 
          println("Exception - Cannot process story for this type")
          None
      }
    }
    
    Await.result(
      cacheRepo.set(
        Cache(
          FeatureTypes.InstaStory.i, 
          account.id, 
          createCacheValue(storyCache, newStories.map(_.id).toSet),
          Some(new DateTime().plusHours(24).getMillis / 1000)
        )
      ), Duration(10, TimeUnit.SECONDS))

    telegramResponses
  }
  
  private def createCacheValue(oldCache: Set[String], newCache: Set[String]): String =
    (oldCache ++ newCache).foldLeft("")((res, i) => s"$res,$i")
    

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
