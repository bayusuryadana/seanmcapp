package com.seanmcapp.util.requestbuilder

import java.io.File
import java.nio.file.Files

import scalaj.http.MultiPart
import com.seanmcapp.config.{StorageConf, TelegramConf}
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.encoder.{TelegramOutputEncoder, TelegramResponse}
import com.seanmcapp.util.parser.decoder.TelegramInputDecoder

import scala.concurrent.Future

trait TelegramRequestBuilder extends TelegramInputDecoder with TelegramOutputEncoder {

  val http: HttpRequestBuilder

  val telegramConf = TelegramConf()

  def sendPhoto(chatId: Long, photoUrl: String, caption: String): TelegramResponse = {
    val urlString = telegramConf.endpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + photoUrl +
      "&caption=" + caption

    val response = http.sendGetRequest(urlString)
    decode[TelegramResponse](response)
  }

  def sendMessage(chatId: Long, text: String): TelegramResponse = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text + "&parse_mode=markdown"
    val response = http.sendGetRequest(urlString)
    decode[TelegramResponse](response)
  }

  def sendPhotoWithFileUpload(chatId: Long, caption: String = "", data: Array[Byte]) : TelegramResponse = {
    val parts = MultiPart("photo", caption, "application/octet-stream", data)
    val params = Some(ParamMap(Map("chat_id" -> String.valueOf(chatId), "caption" -> caption)))

    val response = http.sendRequest(telegramConf.endpoint + "/sendphoto", params, multiPart = Some(parts))
    decode[TelegramResponse](response)
  }

}
