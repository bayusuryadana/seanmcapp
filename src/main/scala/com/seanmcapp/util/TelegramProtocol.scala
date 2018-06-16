package com.seanmcapp.util

import com.seanmcapp.model._
import spray.json._

object TelegramProtocol extends DefaultJsonProtocol {

  object TelegramUpdateFormat extends RootJsonFormat[TelegramUpdate] {
    override def write(obj: TelegramUpdate) = {
      (obj.message, obj.callbackQuery) match {
        case (Some(message), Some(cb)) => JsObject("message" -> TelegramMessageFormat.write(message), "callback_query" -> TelegramCallbackQueryFormat.write(cb))
        case (Some(message), _) => JsObject("message" -> TelegramMessageFormat.write(message))
        case (_, Some(cb)) => JsObject("callback_query" -> TelegramCallbackQueryFormat.write(cb))
        case (_,_) => JsObject()
      }
    }

    override def read(value: JsValue) = {
      val message = value.asJsObject.getFields("message").map(_.asJsObject).headOption.map(m => TelegramMessageFormat.read(m))
      val callbackQuery = value.asJsObject.getFields("callback_query").map(_.asJsObject).headOption.map(cb => TelegramCallbackQueryFormat.read(cb))
      TelegramUpdate(message, callbackQuery)
    }
  }

  object TelegramMessageFormat extends RootJsonFormat[TelegramMessage] {

    override def write(obj: TelegramMessage) = {
      JsObject(
        "from" -> TelegramUserFormat.write(obj.from),
        "chat" -> TelegramChatFormat.write(obj.chat),
        "text" -> JsString(obj.text),
        "entities" -> obj.entities.map(TelegramMessageEntityFormat.write).toJson
      )
    }

    override def read(value: JsValue) = {
      val entitiesValue = value.asJsObject.getFields("entities") match {
        case Seq(JsArray(entities)) => entities.map(TelegramMessageEntityFormat.read)
        case _ => Seq.empty
      }

      value.asJsObject.getFields("from", "chat", "text") match {
        case Seq(from, chat, JsString(text)) =>
          TelegramMessage(
            TelegramUserFormat.read(from),
            TelegramChatFormat.read(chat),
            text,
            entitiesValue
          )

        case _ => throw new DeserializationException("failed to deserialize TelegramMessage")
      }
    }

  }

  object TelegramCallbackQueryFormat extends RootJsonFormat[TelegramCallbackQuery] {

    override def write(obj: TelegramCallbackQuery) = {
      JsObject(
        "id" -> JsString(obj.id),
        "from" -> TelegramUserFormat.write(obj.from),
        "data" -> JsString(obj.data)
      )
    }

    override def read(value: JsValue) = {
      value.asJsObject.getFields("id", "from", "data") match {
        case Seq(JsString(id), from, JsString(data)) =>
          TelegramCallbackQuery(
            id,
            TelegramUserFormat.read(from),
            data
          )

        case _ => throw new DeserializationException("failed to deserialize TelegramCallbackQuery")
      }
    }

  }

  object TelegramUserFormat extends RootJsonFormat[TelegramUser] {

    override def write(obj: TelegramUser) = {
      (obj.lastName, obj.username) match {
        case (Some(lastName), Some(username)) =>
          JsObject("id" -> JsNumber(obj.id), "firstName" -> JsString(obj.firstName), "lastName" -> JsString(lastName), "username" -> JsString(username))
        case (Some(lastName), _) =>
          JsObject("id" -> JsNumber(obj.id), "firstName" -> JsString(obj.firstName), "lastName" -> JsString(lastName))
        case (_, Some(username)) =>
          JsObject("id" -> JsNumber(obj.id), "firstName" -> JsString(obj.firstName), "username" -> JsString(username))
        case (_, _) =>
          JsObject("id" -> JsNumber(obj.id), "firstName" -> JsString(obj.firstName))
      }

    }

    override def read(value: JsValue) = {
      val valueJsObject = value.asJsObject
      val lastNameResult = valueJsObject.getFields("last_name") match {
        case Seq(JsString(lastName)) => Some(lastName)
        case _ => None
      }
      val usernameResult = valueJsObject.getFields("username") match {
        case Seq(JsString(username)) => Some(username)
        case _ => None
      }
      valueJsObject.getFields("id", "first_name") match {
        case Seq(JsNumber(id), JsString(firstName)) =>
          TelegramUser(id.toLong, firstName, lastNameResult, usernameResult)

        case _ => throw new DeserializationException("failed to deserialize TelegramUser")
      }
    }

  }

  object TelegramChatFormat extends RootJsonFormat[TelegramChat] {

    override def write(obj: TelegramChat) = {
      obj.title match {
        case Some(title) =>
          JsObject("id" -> JsNumber(obj.id), "chatType" -> JsString(obj.chatType), "title" -> JsString(title))
        case _ =>
          JsObject("id" -> JsNumber(obj.id), "chatType" -> JsString(obj.chatType))
      }
    }

    override def read(value: JsValue) = {
      value.asJsObject.getFields("id", "type", "title") match {
        case Seq(JsNumber(id), JsString(chatType), JsString(title)) =>
          TelegramChat(id.toLong, chatType, Some(title))
        case Seq(JsNumber(id), JsString(chatType)) =>
          TelegramChat(id.toLong, chatType, None)
        case _ => throw new DeserializationException("failed to deserialize TelegramChat")
      }
    }

  }

  object TelegramMessageEntityFormat extends RootJsonFormat[TelegramMessageEntity] {

    override def write(obj: TelegramMessageEntity) = {
      JsObject("entityType" -> JsString(obj.entityType), "offset" -> JsNumber(obj.offset), "length" -> JsNumber(obj.length))
    }

    override def read(value: JsValue) = {
      value.asJsObject.getFields("type", "offset", "length") match {
        case Seq(JsString(entityType), JsNumber(offset), JsNumber(length)) =>
          TelegramMessageEntity(entityType, offset.toInt, length.toInt)
        case _ => throw new DeserializationException("failed to deserialize TelegramMessageEntity")
      }
    }

  }

}
