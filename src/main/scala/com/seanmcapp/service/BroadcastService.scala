package com.seanmcapp.service

import akka.actor.ActorSystem
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.seanmcapp.config.BroadcastConf
import com.seanmcapp.external.TelegramClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
case class BroadcastOutput(success: Boolean, reason: Option[String])

class BroadcastService(telegramClient: TelegramClient) {

  private val SECRET_KEY = BroadcastConf().secretKey
  private val CAPTION = "caption"
  private val CHAT_ID = "chat_id"

  def broadcastWithPhoto(byteSource: Source[ByteString, Any], formFields: Map[String, String])
                        (implicit system: ActorSystem, secretKey: String) : Future[BroadcastOutput] = {
    secretKey match {
      case SECRET_KEY =>
        byteSource.runFold(ByteString.empty)(_ ++ _).map { byteString =>
          val dataByteArray = byteString.toArray
          val captionOpt = formFields.get(CAPTION)
          val chatIdOpt = formFields.get(CHAT_ID)
          val response = for {
            caption <- captionOpt
            chatId <- chatIdOpt.map(_.toLong)
          } yield {
            val response = telegramClient.sendPhotoWithFileUpload(chatId, caption, dataByteArray)
            BroadcastOutput(response.ok, None)
          }
          response.getOrElse(BroadcastOutput(false, Some("caption or chat_id is invalid")))
        }
      case _ => Future.successful(BroadcastOutput(false, Some("secretKey is invalid")))
    }
  }

}
