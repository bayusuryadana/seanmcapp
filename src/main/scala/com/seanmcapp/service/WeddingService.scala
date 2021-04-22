package com.seanmcapp.service

import com.seanmcapp.external.{TelegramClient, TelegramResponse, TelegramUpdate}
import com.seanmcapp.repository.{Wedding, WeddingRepo}
import io.circe.syntax._
import com.seanmcapp.external._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class InlineKeyboardMarkup(text: String, url: String, callback_data: String)

class WeddingService(weddingRepo: WeddingRepo, telegramClient: TelegramClient) extends ScheduledTask {

  private val customEndpoint = "https://api.telegram.org/bot1764501291:AAFSDeRoYl6o8DhFLopN86QDVtwIdxeimsI"

  def receive(telegramUpdate: TelegramUpdate): Option[TelegramResponse] = {
    println(telegramUpdate.toString)
    val message = telegramUpdate.message.getOrElse(throw new Exception("This request is does not have a message"))
    val chatId = message.chat.id
    val userId = message.from.id
    val userFullName = message.from.first_name + " " + message.from.last_name.getOrElse("")

    message.entities.getOrElse(List.empty).headOption match {
      case Some(entity) =>
        val command = message.text.map(_
          .substring(entity.offset, entity.offset + entity.length)
          .stripSuffix("@BastianLyviaBot")
        )
        command match {
          case Some(s) if s == "/start" =>
            // save user to DB
            weddingRepo.get(userId).map { w =>
              if (w.isEmpty) weddingRepo.insert(Wedding(userId, userFullName, null))
            }

            val welcomeMessage =
              s"""
                 |Hi $userFullName! Thank you for subscribing to BastianLyviaBot!
                 |#BastianLyvia Holy Matrimony will be streaming LIVE on May 29th,2021 at 11.00 AM WIB (12.00 PM SGT)
                 |Reminder will only be sent twice: one hour before livestream and when livestream start.
                 |No spam guaranteed.
                 |Stay tune and be part of #BastianLyvia Wedding Day!
                 |Livestream:
                 |{LINK}
                 |""".stripMargin
            val telegramRes = telegramClient.sendMessage(chatId, welcomeMessage, Some(customEndpoint))
            Some(telegramRes)
          case Some(s) =>
            val authorisedIds = Set(274852283L, 869577150L)
            val args = s.split("_")
            if (args.length == 2 && args(0) == "/test" && authorisedIds.contains(chatId)) {
              val replyMarkup = InlineKeyboardMarkup("click here to livestream !", "http://www.google.com", "count").asJson.encode
              args(1) match {
                case "1" => Some(telegramClient.sendMessage(chatId, reminder1Message, Some(customEndpoint), Some(replyMarkup)))
                case "2" => Some(telegramClient.sendMessage(chatId, reminderStartMessage, Some(customEndpoint), Some(replyMarkup)))
                case "3" => Some(telegramClient.sendMessage(chatId, thxMessage, Some(customEndpoint)))
                case _ => None
              }
            } else None
        }
      case _ =>
        println("[ERROR] No entities (command) found")
        None
    }
  }
  
  override def run(): Future[Seq[TelegramResponse]] = {
    // May 29th,2021 at 11.00 AM WIB (12.00 PM SGT)
    val day = 29
    val month = 5
    val year = 2021
    val hour = 11
    val currentTime = getCurrentTime

    for {
      guests <- weddingRepo.getAll
    } yield {
      val guestIds = guests.map(_.id)
      if ((currentTime.getDayOfMonth == day) && (currentTime.getMonthOfYear == month) && (currentTime.getYear == year)) {
        currentTime.getHourOfDay match {
          case h if h == hour-1 => guestIds.map(id => telegramClient.sendMessage(id, reminder1Message, Some(customEndpoint)))
          case h if h == hour => guestIds.map(id => telegramClient.sendMessage(id, reminderStartMessage, Some(customEndpoint)))
          case h if h == hour + 2 => guestIds.map(id => telegramClient.sendMessage(id, thxMessage, Some(customEndpoint)))
          case _ => Seq.empty[TelegramResponse]
        }
      } else Seq.empty[TelegramResponse]
    }
  }
  
  private val reminder1Message =
    s"""
       |One hour before #BastianLyvia Holy Matrimony!
       |Are you excited? Bastian and Lyvia would be thrilled to know you’re joining!
       |{LINK}
       |Be part of #BastianLyvia Wedding Day by joining and don’t forget to leave comment!
       |""".stripMargin
       
  private val reminderStartMessage =
    s"""
       |#BastianLyvia Holy Matrimony is streaming NOW!
       |{LINK}
       |Be part of #BastianLyvia Wedding Day by joining and don’t forget to leave comment!
       |""".stripMargin
       
  private val thxMessage = 
    s"""
       |Thank you for being part of #BastianLyvia Wedding Day!
       |Feel free to share #BastianLyvia moments from your social media or you can leave message directly to them. 
       |I’m just a bot that is not sophisticated enough to filter all your message :(
       |Have a nice day!
       |""".stripMargin
}
