package com.seanmcapp.util.requestbuilder

import scalaj.http.{Http, HttpOptions, HttpRequest}

trait InstagramRequestBuilder extends HttpRequestBuilder {

  override val baseUrl = "https://www.instagram.com/"

  def getInstagramPageRequest(account: String, lastId: Option[String]): HttpRequest = {

    Http(baseUrl + account + "/")
      .params(Seq(("__a", "1"), ("max_id", lastId.getOrElse(""))))
      .option(HttpOptions.allowUnsafeSSL)
  }

  def getInstagramHome: HttpRequest = {
    Http("https://www.instagram.com").method("HEAD")
  }

  def getInstagramAuth(username: String, password: String, csrfToken: String): HttpRequest = {
    Http(baseUrl + "accounts/login/ajax/")
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

}
