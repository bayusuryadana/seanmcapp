package com.seanmcapp.service

import com.seanmcapp.config.InstagramConf
import com.seanmcapp.helper.{HttpRequestBuilder, JsonProtocol}
import com.seanmcapp.model._
import com.seanmcapp.repository._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex
import scalaj.http.HttpResponse
import spray.json._

object InstagramService extends HttpRequestBuilder with JsonProtocol {

  private val instagramConf = InstagramConf()

  def flow(account: String): Future[InstagramUser] = {

    val auth = None //InstagramService.getAuth(instagramConf.username, instagramConf.password)
    val fetchResult = getPage(account, auth, None)
    val photoRepoFuture = PhotoRepo.getAll
    val customerRepoFuture = CustomerRepo.getAllSubscribedCust

    for {
      photoRepo <- photoRepoFuture
      customerRepo <- customerRepoFuture
    } yield {
      val regexFilter = "[\\w]+'\\d\\d".r
      val unsavedPhotos = fetchResult.nodes
        .filterNot(item => photoRepo.contains(item.id) || regexFilter.findFirstIn(item.caption).isEmpty)

      unsavedPhotos.map { node =>
        val photo = Photo(node.id, node.thumbnailSrc, node.date, node.caption)
        PhotoRepo.update(photo)

        customerRepo.map { subscriber =>
          TelegramService.sendPhoto(subscriber.id, photo.thumbnailSrc, "bahan ciol baru : " + photo.caption)
        }

        // uncomment this for dev env
        // TelegramService.sendPhoto(274852283L, photo.thumbnailSrc, "bahan ciol baru : " + photo.caption)
      }
      fetchResult.copy(nodes = unsavedPhotos)
    }
  }

  def getPage(account: String,
              auth: Option[InstagramAuthToken] = None,
              lastId: Option[String] = None): InstagramUser = {

    val request = getInstagramPageRequest(account, auth, lastId)
    val response = request.asString
    val instagramUser = response.body.parseJson.convertTo[InstagramUser]
    val tmpResult = instagramUser.nodes
    val lastIdRes = tmpResult.lastOption.map(_.id)
    val nextResult = if (tmpResult.nonEmpty)
      getPage(account, auth, lastIdRes).nodes
    else
      Seq.empty

    instagramUser.copy(nodes = tmpResult ++ nextResult)
  }

  def getAuth(username: String, password: String): Option[InstagramAuthToken] = {
    val csrftokenPattern = new Regex("(?<=csrftoken=)[^;]+")
    val sessionidPattern = new Regex("(?<=sessionid=)[^;]+")

    //init
    val requestInit = getInstagramHome
    val responseInit = requestInit.asString
    val csrftokenInit = getPatternFromCookie(responseInit, csrftokenPattern)

    //auth
    csrftokenInit match {
      case Some(tokenInit:String) =>
        val requestAuth = getInstagramAuth(username, password, tokenInit)
        val responseAuth = requestAuth.asString
        val csrftoken = getPatternFromCookie(responseAuth, csrftokenPattern)
        val sessionId = getPatternFromCookie(responseAuth, sessionidPattern)

        (csrftoken, sessionId) match {
          case (Some(token:String), Some(session:String)) => Some(InstagramAuthToken(token, session))
          case _ => None
        }
      case _ => None
    }
  }

  private def getPatternFromCookie(input: HttpResponse[String], pattern: Regex): Option[String] = {
    val cookie = input.headers("Set-Cookie").mkString("; ")
    pattern.findFirstIn(cookie)
  }
}