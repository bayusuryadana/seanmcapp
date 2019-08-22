package com.seanmcapp.util.requestbuilder

import com.seanmcapp.config.{StorageConf, TelegramConf}
import com.seanmcapp.repository.instagram.Photo
import com.seanmcapp.util.parser.encoder.{TelegramOutputEncoder, TelegramResponse}
import com.seanmcapp.util.parser.decoder.TelegramInputDecoder

trait TelegramRequestBuilder extends TelegramInputDecoder with TelegramOutputEncoder {

  val http: HttpRequestBuilder

  val telegramConf = TelegramConf()

  def sendPhoto(chatId: Long, photo: Photo): TelegramResponse = {
    val photoId = photo.id
    val storageConf = StorageConf()
    val url = storageConf.host + "/" + storageConf.bucket + "/cbc/" + photoId  + ".jpg"

    val urlString = telegramConf.endpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + url +
      "&caption=" + photo.caption +
      "%0A%40" + photo.account

    val response = http.sendRequest(urlString)
    decode[TelegramResponse](response)
  }

  def sendMessage(chatId: Long, text: String): TelegramResponse = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text + "&parse_mode=markdown"
    val response = http.sendRequest(urlString)
    decode[TelegramResponse](response)
  }

}
