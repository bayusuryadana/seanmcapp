package com.seanmcapp.service

import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.seanmcapp.config.{BroadcastConf, WalletConf}
import com.seanmcapp.util.parser.{BroadcastOutput, BroadcasterCommon}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class BroadcastService(override val http: HttpRequestBuilder) extends BroadcasterCommon with TelegramRequestBuilder {

  private[service] val SECRET_KEY = BroadcastConf().secretKey

  def broadcastWithPhoto(fileInfo: FileInfo, byteSource: Source[ByteString, Any])(implicit mat: Materializer, secretKey: String) : Future[BroadcastOutput] = {
    secretKey match {
      case SECRET_KEY =>
        byteSource.runFold(ByteString.empty)(_ ++ _).map { byteString =>
          val dataByteArray = byteString.toArray
          val response = sendPhotoWithFileUpload(274852283L, data = dataByteArray)
          BroadcastOutput(response.ok)
        }
      case _ => Future.successful(BroadcastOutput(false))
    }
  }

}
