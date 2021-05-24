package com.seanmcapp.service

import com.seanmcapp.external.{InstagramClient, TelegramClient, TelegramResponse}
import com.seanmcapp.repository.{Cache, CacheRepo}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class InstagramPost(id: String, caption: String, media: Seq[InstagramPostChild])
case class InstagramPostChild(isVideo: Boolean, imageURL: String, videoURL: Option[String])

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
        val cacheToUpdate = Cache(s"instapost-$id", (cacheList ++ nodes.map(_.id).toSet).reduce(_ + "," + _), None)
        cacheRepo.set(cacheToUpdate)

        // send update
        val chatId = -1
        nodes.map { node =>
          node.media.map { media =>
            if (media.isVideo) telegramClient.sendVideoWithFileUpload(chatId, url = media.videoURL.get) else
              telegramClient.sendPhotoWithFileUpload(chatId, url = media.imageURL)
          }
          telegramClient.sendMessage(chatId, s"$name\n${node.caption}")
        }
      }
    }

    Future.sequence(resultF).map(_.flatten)
  }

  private def convert(node: InstagramNode): InstagramPost = {
    val id = node.id
    val caption = node.edge_media_to_caption.edges.headOption.map(_.node.text).getOrElse("")
    val media = node.edge_sidecar_to_children.edges.map { edge =>
      InstagramPostChild(edge.node.is_video, edge.node.display_url, edge.node.video_url)
    }
    InstagramPost(id, caption, media)
  }

}
