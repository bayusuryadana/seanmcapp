package com.seanmcapp.model

case class InstagramAuthToken(csrftoken: String, sessionId: String)

case class InstagramNode(id: String, caption: String, thumbnailSrc: String, date: Long)

case class InstagramUser(id: String,
                         biography: String,
                         fullName: String,
                         isPrivate: Boolean,
                         username: String,
                         nodes: Seq[InstagramNode])

case class TelegramMessage(from: TelegramUser, chat: TelegramChat, text: String, entities: Seq[TelegramMessageEntity])

case class TelegramUser(id: Long, firstName: String, lastName: Option[String], username: Option[String])

case class TelegramChat(id: Long, chatType: String, title: Option[String])

case class TelegramMessageEntity(entityType: String, offset: Int, length: Int)

case class Photo(id: String, thumbnailSrc: String, date: Long, caption: String)

case class Customer(id: Long, name: String, isSubscribed: Boolean, hitCount: Long)

case class BroadcastMessage(message: String, key: String)

