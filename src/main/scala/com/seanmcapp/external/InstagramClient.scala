package com.seanmcapp.external

import com.seanmcapp.InstagramConf
import com.seanmcapp.util.MemoryCache
import io.circe.syntax._
import org.joda.time.DateTime
import scalacache.Cache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.{Duration, FiniteDuration}

case class InstagramStoryRequestParameter(reel_ids: Seq[String], precomposed_overlay: Boolean)

class InstagramClient(http: HttpRequestClient) extends MemoryCache {

  val instagramConf = InstagramConf()
  implicit val sessionCache: Cache[String] = createCache[String]
  private val duration: FiniteDuration = Duration(365, TimeUnit.DAYS)

  def postLogin(): String = {
    memoizeSync(Some(duration)) {
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
  }

  def getAccountResponse(accountId: String): InstagramAccountResponse = {
    val url = s"https://www.instagram.com/$accountId/?__a=1"
    val httpResponse = http.sendGetRequest(url)
    decode[InstagramAccountResponse](httpResponse)
  }

  def getAllPosts(userId: String, endCursor: Option[String], sessionId: String): Seq[InstagramNode] = {
    val instagramResponse = {
      val numberOfBatch = 50
      val params = InstagramRequestParameter(userId, numberOfBatch, endCursor).asJson.encode
      val url = s"https://www.instagram.com/graphql/query/?query_hash=18a7b935ab438c4514b1f742d8fa07a7&variables=$params"
      val headers = getHeaders(sessionId)
      val httpResponse = http.sendGetRequest(url, headers = headers)
      decode[InstagramResponse](httpResponse)
    }
    val instagramMedia = instagramResponse.data.user.edge_owner_to_timeline_media
    val result = instagramMedia.edges.map(_.node)
    val endResult = if (instagramMedia.page_info.has_next_page && instagramMedia.page_info.end_cursor.isDefined) {
      result ++ getAllPosts(userId, instagramMedia.page_info.end_cursor, sessionId)
    } else {
      result
    }
    endResult
  }

  def getStories(userId: String, sessionId: String): InstagramStoryResponse = {
    val params = InstagramStoryRequestParameter(Seq(userId), false).asJson.encode
    val url = s"https://www.instagram.com/graphql/query/?query_hash=c9c56db64beb4c9dea2d17740d0259d9&variables=$params"
    val headers = getHeaders(sessionId)
    val httpResponse = http.sendGetRequest(url, headers = headers)
    decode[InstagramStoryResponse](httpResponse)
  }
  
  private def getHeaders(sessionId: String): Option[HeaderMap] = 
    Some(HeaderMap(Map("cookie" -> s"sessionid=$sessionId")))

}
