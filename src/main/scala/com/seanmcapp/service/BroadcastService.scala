package com.seanmcapp.service

import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.seanmcapp.config.BroadcastConf
import com.seanmcapp.util.parser.{BroadcastOutput, BroadcasterCommon}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BroadcastService(override val http: HttpRequestBuilder) extends BroadcasterCommon with TelegramRequestBuilder {

  private val SECRET_KEY = BroadcastConf().secretKey
  private val CAPTION = "caption"
  private val CHAT_ID = "chat_id"

  def broadcastWithPhoto(byteSource: Source[ByteString, Any], formFields: Map[String, String])
                        (implicit mat: Materializer, secretKey: String) : Future[BroadcastOutput] = {
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
            val response = sendPhotoWithFileUpload(chatId, caption, dataByteArray)
            BroadcastOutput(response.ok, None)
          }
          response.getOrElse(BroadcastOutput(false, Some("caption or chat_id is invalid")))
        }
      case _ => Future.successful(BroadcastOutput(false, Some("secretKey is invalid")))
    }
  }

}
