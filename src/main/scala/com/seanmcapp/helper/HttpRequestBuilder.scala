package com.seanmcapp.helper

import com.seanmcapp.model.Photo

import scalaj.http.{Http, HttpOptions, HttpRequest}

trait HttpRequestBuilder {

  def getInstagramPageRequest(account: String,
                              lastId: Option[String]): HttpRequest = {

    Http("https://www.instagram.com/" + account + "/")
      .params(Seq(("__a", "1"), ("max_id", lastId.getOrElse(""))))
      .option(HttpOptions.allowUnsafeSSL)
  }

  def getInstagramHome: HttpRequest = {
    Http("https://www.instagram.com").method("HEAD")
  }

  def getInstagramAuth(username: String, password: String, csrfToken: String): HttpRequest = {
    Http("https://www.instagram.com/accounts/login/ajax/")
      .postData("username=" + username + "&password=" + password)
      .headers(
        Seq(
          ("X-Requested-With", "XMLHttpRequest"),
          ("X-CSRFToken", csrfToken),
          ("Referer", "https://www.instagram.com/"),
          ("Cookie", "csrftoken=" + csrfToken)
        ))
      .option(HttpOptions.allowUnsafeSSL)
  }

  def getTelegramSendPhoto(telegramBotEndpoint: String, chatId: Long, photo: Photo, prefix: String =""): HttpRequest = {
    val inlineKeyboard =
      """
        |{
        |inline_keyboard: [
        |[
        |{text: "1", callback_data: "1:#{photo['id']}"},
        |{text: "2", callback_data: "2:#{photo['id']}"},
        |{text: "3", callback_data: "3:#{photo['id']}"},
        |{text: "4", callback_data: "4:#{photo['id']}"},
        |{text: "5", callback_data: "5:#{photo['id']}"}
        |]
        |]
        |}
      """.stripMargin

    val urlString = telegramBotEndpoint + "/sendphoto" +
      "?chat_id=" + chatId +
      "&photo=" + photo.thumbnailSrc +
      "&caption=" + prefix + photo.caption +
      "%0A%40" +
      "&reply_markup=" + inlineKeyboard

    Http(urlString)
  }

  def getTelegramSendMessege(telegramBotEndpoint: String, chatId: Long, text: String): HttpRequest = {
    val urlString = telegramBotEndpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text
    Http(urlString)
  }

  def getAnswerCallbackQuery(telegramBotEndPoint: String, queryId: String, notificationText: String) = {
    val urlString = telegramBotEndPoint + "/answerCallbackQuery?callback_query_id=" + queryId + "&text=" + notificationText
    Http(urlString)
  }
}
