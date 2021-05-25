package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.{Cache, CacheRepo}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class InstagramPost(id: String, caption: String, media: Seq[InstagramPostChild])
case class InstagramPostChild(isVideo: Boolean, sourceURL: String)

class InstagramPostService(instagramClient: InstagramClient, telegramClient: TelegramClient, cacheRepo: CacheRepo) extends ScheduledTask {

  override def run: Any = fetch()

  def fetch(sessionIdOpt: Option[String] = None): Future[Seq[TelegramResponse]] = {
    val sessionId = sessionIdOpt.getOrElse(instagramClient.postLogin())
    val accountMap = Map(
      "Alvida" -> "302844663",
      "Buggy" -> "277395688",
      "Gecko Moria" -> "5646204159"
    )

    val resultF = accountMap.toSeq.map { case (name, id) =>
      cacheRepo.get(s"instapost-$id").map { cacheOpt =>
        val cacheList = cacheOpt.map(_.value.split(",").toSet).getOrElse(Set.empty[String])
        val nodes = instagramClient.getAllPost(id, None, sessionId).map(convert).filterNot(p => cacheList.contains(p.id))

        // saving cache
        val newCacheString = (cacheList ++ nodes.map(_.id).toSet).reduce(_ + "," + _)
        val cacheToUpdate = Cache(s"instapost-$id", newCacheString, None)
        cacheRepo.set(cacheToUpdate)

        // send update
        val chatId = -1001359004262L
        nodes.map { node =>
          node.media.map { media =>
            if (media.isVideo) telegramClient.sendVideoWithFileUpload(chatId, data = telegramClient.getDataByteFromUrl(media.sourceURL)) else
              telegramClient.sendPhotoWithFileUpload(chatId, data = telegramClient.getDataByteFromUrl(media.sourceURL))
          }
          telegramClient.sendMessage(chatId, s"POST $name\n${node.caption}")
        }
      }
    }

    Future.sequence(resultF).map(_.flatten)
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
