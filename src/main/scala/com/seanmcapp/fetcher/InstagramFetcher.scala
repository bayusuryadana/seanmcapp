package com.seanmcapp.fetcher

import com.seanmcapp.api.Service
import com.seanmcapp.config.InstagramConf
import com.seanmcapp.repository._
import com.seanmcapp.util.parser._
import com.seanmcapp.util.requestbuilder.InstagramRequest
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait InstagramFetcher extends Service with InstagramRequest {

  case class InstagramAuthToken(csrftoken: String, sessionId: String)
  import com.seanmcapp.util.parser.InstagramJson._

  def flow: Future[JsValue] = {
    val accountsFuture = accountRepo.getAll
    for {
      accounts <- accountsFuture
      results <- fetch(accounts)
    } yield {
      results
    }
  }

  private def fetch(accounts: Seq[Account]): Future[JsValue] = {
    val authOption = getAuth
    println("[AUTH] " + authOption)

    if (authOption.isDefined) {
      val auth = authOption.get
      val results = accounts.map { account =>
        val latestPhotoFuture = photoRepo.getLatest(account.name)
        val allPhotoFuture = photoRepo.getAll(account.name)
        for {
          latestPhoto <- latestPhotoFuture
          allPhotoSet <- allPhotoFuture
        } yield {
          println("[START] fetching " + account.name)
          val fetchResult = getPage(account, auth, None, latestPhoto.map(_.date).getOrElse(0))

          def regexResult = (node: InstagramNodeResult) => account.regex.r.findFirstIn(node.caption)

          val unsavedResult = fetchResult
            .filter(node => !(allPhotoSet.contains(node.id) || regexResult(node).isEmpty))
            .map(node => Photo(node.id, node.thumbnailSrc, node.date, regexResult(node).get, account.name))

          println("[SAVING] saving " + account.name + " " + unsavedResult.size + " photo(s)")
          photoRepo.insert(unsavedResult).map(_ => println("[DONE] fetching " + account.name))
          account.name -> unsavedResult
        }
      }
      Future.sequence(results).map(_.toJson)
    } else {
      Future.successful(403.toJson)
    }
  }

  private def getPage(account: Account, auth: InstagramAuthToken, lastId: Option[Long] = None, latestDate: Long): Seq[InstagramNodeResult] = {

    val response = getInstagramPageRequest(account, lastId, auth.csrftoken, auth.sessionId)
    val instagramUpdate = response.body.parseJson.convertTo[InstagramUpdate]
    val result = instagramUpdate.data.user.edgeToOwnerMedia.edges.map{ edge =>
      val node = edge.node
      InstagramNodeResult(node.id.toLong, node.caption.edges.headOption.map(_.node.text).getOrElse(""), node.thumbnailSrc, node.date)
    }
    val lastIdResult = result.lastOption.map(_.id)
    val nextResult = if (result.nonEmpty && latestDate < result.last.date) {
      println(account.name + " :::: " + latestDate + " >< " + result.last.date)
      getPage(account, auth, lastIdResult, latestDate)
    } else Seq.empty

    result ++ nextResult
  }

  def getAuth: Option[InstagramAuthToken] = {

    val csrfRegex = "(?<=csrftoken=)[^;\"]+".r
    val sessionIdRegex = "(?<=sessionid=)[^;\"]+".r

    val initF = getInstagramHome.headers.get("set-cookie").flatMap(r => csrfRegex.findFirstIn(r.reduce(_ + _)))
    val authF = (csrf: String) => {
      // authentication
      val username = InstagramConf().username
      val password = InstagramConf().password
      getInstagramAuth(username, password, csrf).headers.get("set-cookie").map(r => r.reduce(_ + _))
    }

    for {
      init <- initF
      authHeader <- authF(init)
      csrfToken <- csrfRegex.findFirstIn(authHeader)
      sessionId <- sessionIdRegex.findFirstIn(authHeader)
    } yield {
      InstagramAuthToken(csrfToken, sessionId)
    }
  }

}
