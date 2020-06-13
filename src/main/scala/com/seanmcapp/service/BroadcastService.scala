package com.seanmcapp.service

import java.io.File

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.directives.FileInfo
import com.seanmcapp.util.parser.{BroadcastOutput, BroadcasterCommon}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.Future

class BroadcastService(override val http: HttpRequestBuilder) extends BroadcasterCommon with TelegramRequestBuilder {

  def tempDestination(fileInfo: FileInfo): File =
    File.createTempFile(fileInfo.fileName, ".tmp")

  def broadcastWithPhoto() : Future[Option[BroadcastOutput]] = {
//    val response = sendPhotoWithFileUpload(143635997L, file = file)
//    if (response.ok) {
//      BroadcastOutput(200, Some("success"))
//    }
//    Future.successful(BroadcastOutput(false))
    Future.successful(None)
  }

}
