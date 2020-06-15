package com.seanmcapp.service

import java.io.File
import java.util.concurrent.TimeUnit

import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import com.seanmcapp.util.parser.{BroadcastOutput, BroadcasterCommon}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class BroadcastService(override val http: HttpRequestBuilder) extends BroadcasterCommon with TelegramRequestBuilder {

  def tempDestination(fileInfo: FileInfo): File =
    File.createTempFile(fileInfo.fileName, ".tmp")

  def broadcastWithPhoto(fileInfo: FileInfo, byteSource: Source[ByteString, Any])(implicit mat: Materializer) : Future[BroadcastOutput] = {
    val inputStream = byteSource.runWith(StreamConverters.asInputStream(FiniteDuration(3, TimeUnit.SECONDS)))
    val dataByteArray = LazyList.continually(inputStream.read).takeWhile(_ != -1).map(_.toByte).toArray
    val response = sendPhotoWithFileUpload(143635997L, data = dataByteArray)
    Future.successful(BroadcastOutput(response.ok))
  }

}
