package com.seanmcapp.util.requestbuilder

import com.seanmcapp.repository.instagram.Account

import scalaj.http.{Http, HttpOptions, HttpResponse}

trait InstagramRequest {

  val baseUrl = "https://www.instagram.com/"

  def getInstagramPageRequest(account: Account, lastId: Option[Long], csrftoken: String, sessionid: String): HttpResponse[String] = {
    val request = Http(baseUrl + "graphql/query/?query_id=17888483320059182&variables={\"id\":" + account.id + ",\"first\":50,\"after\":" + lastId.getOrElse("null") + "}")
      .header("Cookie", "sessionid=" + sessionid + "; csrftoken=" + csrftoken)
      .option(HttpOptions.allowUnsafeSSL)
    println("[INFO] " + account.name + " | request url: " + request.url)
    request.asString
  }

  def getInstagramHome: HttpResponse[String] = {
    Http(baseUrl + "accounts/login/ajax/").method("HEAD").asString
  }

  def getInstagramAuth(username: String, password: String, csrfToken: String): HttpResponse[String] = {
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
      .asString
  }

}
