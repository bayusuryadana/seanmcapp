package com.seanmcapp.fetcher

import com.seanmcapp.config.InstagramConf
import com.seanmcapp.repository._
import com.seanmcapp.util.parser._
import com.seanmcapp.util.requestbuilder.InstagramRequest
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

abstract class InstagramFetcher extends InstagramRequest {

  val customerRepo: CustomerRepo
  val photoRepo: PhotoRepo
  val accountRepo: AccountRepo

  case class InstagramAuthToken(csrftoken: String, sessionId: String)
  import com.seanmcapp.util.parser.InstagramJson._

  def flow: Future[JsValue] = {
    val accountsFuture = accountRepo.getAll
    val customerRepoFuture = customerRepo.getAllSubscribedCust
    for {
      accounts <- accountsFuture
      customers <- customerRepoFuture
      results <- fetch(accounts, customers)
    } yield {
      results
    }
  }

  private def fetch(accounts: Seq[Account], customers: Seq[Customer]): Future[JsValue] = {
    val auth = getAuth.get // TODO: fix this option
    println("[AUTH] " + auth)

    val results = accounts.map { account =>
      val latestPhotoFuture = photoRepo.getLatest(account.name)
      val allPhotoFuture = photoRepo.getAll(account.name)
      for {
        latestPhoto <- latestPhotoFuture
        allPhotoSet <- allPhotoFuture
      } yield {
        println("[START] fetching " + account.name)
        val fetchResult = getPage(account.id, auth, None, latestPhoto.map(_.date).getOrElse(0))

        def regexResult = (node: InstagramNodeResult) => account.regex.r.findFirstIn(node.caption)

        val unsavedResult = fetchResult
          .filter(node => !(allPhotoSet.contains(node.id) || regexResult(node).isEmpty))
          .map(node => node.copy(caption = regexResult(node).get.replace("\\n", "%0A").replace("#", "%23")))

        println("[SAVING] saving " + account.name)
        unsavedResult.map { node =>
          val photo = Photo(node.id, node.thumbnailSrc, node.date, node.caption, account.name)
          photoRepo.update(photo)

          /*
          customerRepo.map { subscriber =>
            getTelegramSendPhoto(telegramConf.endpoint, subscriber.id, photo, "bahan ciol baru: ")
          }
          */

          // uncomment this for dev env
          // getTelegramSendPhoto(telegramConf.endpoint, 274852283L, photo, "bahan ciol baru: ")
        }

        println("[DONE] fetching " + account.name)
        account.name -> unsavedResult
      }
    }

    Future.sequence(results).map(_.toJson)
  }

  private def getPage(accountId: String, auth: InstagramAuthToken, lastId: Option[String] = None, latestDate: Long): Seq[InstagramNodeResult] = {

    val response = getInstagramPageRequest(accountId, lastId, auth.csrftoken, auth.sessionId)
    val instagramUpdate = response.body.parseJson.convertTo[InstagramUpdate]
    val result = instagramUpdate.data.user.edgeToOwnerMedia.edges.map{ edge =>
      val node = edge.node
      InstagramNodeResult(node.id, node.caption.edges.headOption.map(_.node.text).getOrElse(""), node.thumbnailSrc, node.date)
    }
    val lastIdResult = result.lastOption.map(_.id)
    val nextResult = if (result.nonEmpty && latestDate < result.last.date)
      getPage(accountId, auth, lastIdResult, latestDate)
    else
      Seq.empty

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
