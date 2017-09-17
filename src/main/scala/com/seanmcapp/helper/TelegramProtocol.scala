package com.seanmcapp.helper

import com.seanmcapp.model._
import spray.json._

object TelegramProtocol extends DefaultJsonProtocol {

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
      val jsObject = value.asJsObject.getFields("message").map(_.asJsObject)
        .headOption.getOrElse(throw new DeserializationException("failed to deserialize TelegramUser at 'message'"))

      val entitiesValue = jsObject.getFields("entities") match {
        case Seq(JsArray(entities)) => entities.map(TelegramMessageEntityFormat.read)
        case _ => Seq.empty
      }

      jsObject.getFields("from", "chat", "text") match {
        case Seq(from, chat, JsString(text)) =>
          TelegramMessage(
            TelegramUserFormat.read(from),
            TelegramChatFormat.read(chat),
            text,
            entitiesValue
          )

        case _ => throw new DeserializationException("failed to deserialize TelegramMessageEntity")
      }
    }

  }

}
