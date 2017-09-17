package com.seanmcapp.helper

import com.seanmcapp.config.TelegramConf
import com.seanmcapp.model.InstagramAuthToken

import scalaj.http.{Http, HttpOptions, HttpRequest}

trait HttpRequestBuilder {

  private val telegramConf = TelegramConf()

  def getInstagramPageRequest(account: String,
                              auth: Option[InstagramAuthToken],
                              lastId: Option[String]): HttpRequest = {

    Http("https://www.instagram.com/" + account + "/")
      .params(Seq(("__a", "1"), ("max_id", lastId.getOrElse(""))))
      .headers(auth match {
        case Some(auth: InstagramAuthToken) =>
          Seq(("cookie", "csrftoken=" + auth.csrftoken + "; sessionid=" + auth.sessionId))
        case None => Seq.empty
      })
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

  def getTelegramSendPhoto(chatId: Long, photoUrl: String, caption: String): HttpRequest = {
    val urlString = telegramConf.endpoint + "/sendphoto?chat_id=" + chatId + "&photo=" + photoUrl + "&caption=" + caption
    Http(urlString)
  }

  def getTelegramSendMessege(chatId: Long, text: String): HttpRequest = {
    val urlString = telegramConf.endpoint + "/sendmessage?chat_id=" + chatId + "&text=" + text
    Http(urlString)
  }
}
