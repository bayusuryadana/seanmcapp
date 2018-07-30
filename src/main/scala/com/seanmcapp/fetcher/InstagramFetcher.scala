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

  case class InstagramAuthToken(csrftoken: String, sessionId: String)
  import com.seanmcapp.util.parser.InstagramJson._

  val instagramAccounts = List(
    ("ui.cantik", "[\\w ]+\\. [\\w ]+['’]\\d\\d".r, "1435973343"),
    ("ub.cantik", "[\\w ]+\\. [\\w ]+['’]\\d\\d".r, "4769955827"),
    ("ugmcantik", "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r, "1446646264"),
    ("undip.cantik", "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r, "1816652927"),
    ("unpad.geulis", "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r, "1620166782")
    //("anakstancantik", "".r, "1661936926")
  )

  def flow: Future[JsValue] = {
    val auth = getAuth.get // TODO: fix this option
    println("[AUTH] " + auth)
    Future.sequence(instagramAccounts.map { account =>
      val accountName = account._1
      val accountRegex = account._2
      val accountId = account._3

      val photoRepoFuture = photoRepo.getAll(accountName)
      val customerRepoFuture = customerRepo.getAllSubscribedCust

      println("[START] fetching " + accountName)
      for {
        latestPhoto <- photoRepo.getLatest(accountName)
        photoRepoResult <- photoRepoFuture
        customerRepo <- customerRepoFuture
      } yield {
        val fetchResult = getPage(accountId, auth, None, latestPhoto.map(_.date).getOrElse(0))
        def regexResult = (node: InstagramNodeResult) => accountRegex.findFirstIn(node.caption)
        val unsavedResult = fetchResult
          .filter(node => !(photoRepoResult.contains(node.id) || regexResult(node).isEmpty))
          .map(node => node.copy(caption = regexResult(node).get.replace("\\n","%0A").replace("#", "%23")))

        println("[SAVING] saving " + accountName)
        unsavedResult.map { node =>
          val photo = Photo(node.id, node.thumbnailSrc, node.date, node.caption, accountName)
          photoRepo.update(photo)

          /*
          customerRepo.map { subscriber =>
            getTelegramSendPhoto(telegramConf.endpoint, subscriber.id, photo, "bahan ciol baru: ")
          }
          */

          // uncomment this for dev env
          // getTelegramSendPhoto(telegramConf.endpoint, 274852283L, photo, "bahan ciol baru: ")
        }

        println("[DONE] fetching " + accountName)
        accountName -> unsavedResult
      }
    }).map(_.toJson)
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
