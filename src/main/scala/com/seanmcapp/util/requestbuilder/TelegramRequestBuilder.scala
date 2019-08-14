package com.seanmcapp.util.requestbuilder

import com.seanmcapp.config.{StorageConf, TelegramConf}
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.TelegramResponse
import scalaj.http.Http
import spray.json._

trait TelegramRequestBuilder {

  val telegramConf = TelegramConf()

  import com.seanmcapp.util.parser.TelegramJson._

  def sendPhoto(chatId: Long, photo: Photo): TelegramResponse = {
    val photoId = photo.id
    val storageConf = StorageConf()
    val url = storageConf.host + "/" + storageConf.bucket + "/cbc/" + photoId  + ".jpg"

    val urlString = telegramConf.endpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + url +
      "&caption=" + photo.caption +
      "%0A%40" + photo.account
    Http(urlString).asString.body.parseJson.convertTo[TelegramResponse]
  }

  def sendMessage(chatId: Long, text: String): TelegramResponse = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text + "&parse_mode=markdown"
    Http(urlString).asString.body.parseJson.convertTo[TelegramResponse]
  }

}
