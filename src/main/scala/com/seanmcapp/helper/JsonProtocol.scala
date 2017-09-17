package com.seanmcapp.helper

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.seanmcapp.model._

import scala.reflect.classTag
import spray.json._

trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val photoFormat = jsonFormat4(Photo)

  implicit val instagramNodeFormat = InstagramProtocol.InstagramNodeFormat

  implicit val instagamUserFormat = InstagramProtocol.InstagramUserFormat

  implicit val telegramUserFormat = TelegramProtocol.TelegramUserFormat

  implicit val telegramChatFormat = TelegramProtocol.TelegramChatFormat

  implicit val telegramMessageEntityFormat = TelegramProtocol.TelegramMessageEntityFormat

  implicit val telegramMessageFormat = TelegramProtocol.TelegramMessageFormat

}