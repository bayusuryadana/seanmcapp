package com.seanmcapp.fetcher

import com.seanmcapp.config.InstagramConf
import com.seanmcapp.repository._
import com.seanmcapp.util.parser.InstagramUser
import com.seanmcapp.util.requestbuilder.InstagramRequestBuilder
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InstagramFetcher(customerRepo: CustomerRepo, photoRepo: PhotoRepo) extends InstagramRequestBuilder {

  case class InstagramAuthToken(csrftoken: String, sessionId: String)
  import com.seanmcapp.util.parser.InstagramJson._

  private val instagramAccounts = List(
    ("ui.cantik", "[\\w. ]+[\\w]'\\d\\d".r, "1435973343"),
    ("ugmcantik", "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r, "1446646264"),
    ("undip.cantik", "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r, "1816652927"),
    ("unpad.geulis", "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r, "1620166782"),
    ("unsoedcantik", "[a-zA-Z ]+\\,[a-zA-Z ]+(\\'| )[\\d]+".r, "1457526826")
  )

  def flow: Future[JsValue] = {
    val auth = getAuth
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
        fetchResult <- getPage(accountId, auth.get, None, latestPhoto.map(_.date).getOrElse(0))
        photoRepoResult <- photoRepoFuture
        customerRepo <- customerRepoFuture
      } yield {
        val regexFilter = accountRegex
        val unsavedPhotos = fetchResult.nodes.collect {
          case item if !(photoRepoResult.contains(item.id) || regexFilter.findFirstIn(item.caption).isEmpty) =>
            item.copy(caption = regexFilter.findFirstIn(item.caption).get
              .replace("\\n","%0A")
              .replace("#", "%23"))
        }

        unsavedPhotos.map { node =>
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
        fetchResult.copy(nodes = unsavedPhotos).toJson
      }
    }).map(_.toJson)
  }

  private def getPage(accountId: String,
                      auth: InstagramAuthToken,
                      lastId: Option[String] = None,
                      latestDate: Long): InstagramUser = {

    val request = getInstagramPageRequest(accountId, lastId)
    val response = request.asString
    val instagramUser = response.body.parseJson.convertTo[InstagramUser]
    val tmpResult = instagramUser.nodes
    val lastIdRes = tmpResult.lastOption.map(_.id)
    val nextResult = if (tmpResult.nonEmpty)
      getPage(accountId, auth, lastIdRes, latestDate).nodes
    else
      Seq.empty

    instagramUser.copy(nodes = tmpResult ++ nextResult)
  }

  def getAuth: Option[InstagramAuthToken] = {

    val csrfRegex = "(?<=csrftoken=)[^;]+".r
    val sessionIdRegex = "(?<=sessionid=)[^;]+".r

    val initF = getInstagramHome.asString.headers.get("set-cookie").flatMap(r => csrfRegex.findFirstIn(r.reduce(_ + _)))
    val authF = (csrf: String) => {
      // authentication
      val username = InstagramConf().username
      val password = InstagramConf().password
      getInstagramAuth(username, password, csrf).asString.headers.get("set-cookie").map(r => r.reduce(_ + _))
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
