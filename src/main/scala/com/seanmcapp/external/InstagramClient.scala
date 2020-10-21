package com.seanmcapp.external

import com.seanmcapp.InstagramConf
import com.seanmcapp.service._
import io.circe.syntax._
import org.joda.time.DateTime

class InstagramClient(http: HttpRequestClient) {

  val instagramConf = InstagramConf()

  def postLogin(): String = {
    val initUrl = s"https://www.instagram.com/accounts/login/"
    val initResponse = http.sendGetRequest(initUrl)
    val r = "\"csrf_token\":\"(.*?)\"".r
    val csrfString = s"{${r.findFirstIn(initResponse).getOrElse(throw new Exception("Token not found"))}}"
    val csrfToken = decode[InstagramCsrfToken](csrfString)

    val url = "https://www.instagram.com/accounts/login/ajax/"
    val time = DateTime.now.getMillis / 1000
    val postForm = Map(
      "username" -> instagramConf.username,
      "enc_password" -> s"#PWD_INSTAGRAM_BROWSER:0:$time:${instagramConf.password}",
      "queryParams" -> "{}",
      "optIntoOneTap" -> "false"
    ).toSeq
    val headers = HeaderMap(Map(
      "user-agent" -> "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36",
      "x-requested-with" -> "XMLHttpRequest",
      "referer" -> "https://www.instagram.com/accounts/login/",
      "x-csrftoken" -> csrfToken.csrf_token
    ))
    val response = http.sendRequest(url, postForm = Some(postForm), headers = Some(headers))
    val cookie = response.headers.getOrElse("Set-Cookie", throw new Exception("Cookie not found")).reduce(_ + _)
    val regex = "sessionid=(.*?);".r
    val session = regex.findFirstIn(cookie).getOrElse(throw new Exception("Cookie not found"))

    session.stripPrefix("sessionid=").stripSuffix(";")
  }

  def getAccountResponse(accountId: String): InstagramAccountResponse = {
    val url = s"https://www.instagram.com/$accountId/?__a=1"
    val httpResponse = http.sendGetRequest(url)
    decode[InstagramAccountResponse](httpResponse)
  }

  def getPhotos(userId: String, endCursor: Option[String], sessionId: String): InstagramResponse = {
    val numberOfBatch = 50
    val params = InstagramRequestParameter(userId, numberOfBatch, endCursor).asJson.encode
    val url = s"https://www.instagram.com/graphql/query/?query_hash=18a7b935ab438c4514b1f742d8fa07a7&variables=$params"
    val headers = Some(HeaderMap(Map("cookie" -> s"sessionid=$sessionId")))
    val httpResponse = http.sendGetRequest(url, headers = headers)
    decode[InstagramResponse](httpResponse)
  }

  def getStories(userId: String, sessionId: String): InstagramStoryResponse = {
    val params = InstagramStoryRequestParameter(Seq(userId), false).asJson.encode
    val url = s"https://www.instagram.com/graphql/query/?query_hash=c9c56db64beb4c9dea2d17740d0259d9&variables=$params"
    val headers = Some(HeaderMap(Map("cookie" -> s"sessionid=$sessionId")))
    val httpResponse = http.sendGetRequest(url, headers = headers)
    decode[InstagramStoryResponse](httpResponse)
  }

}
