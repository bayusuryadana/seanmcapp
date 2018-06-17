package com.seanmcapp.fetcher

import com.seanmcapp.repository._
import com.seanmcapp.util.parser.InstagramUser
import com.seanmcapp.util.requestbuilder.InstagramRequestBuilder
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class InstagramFetcher(customerRepo: CustomerRepo, photoRepo: PhotoRepo) extends InstagramRequestBuilder {

  case class InstagramAuthToken(csrftoken: String, sessionId: String)
  import com.seanmcapp.util.parser.InstagramJson._

  private val instagramAccounts = Map(
    "ui.cantik" -> "[\\w. ]+[\\w]'\\d\\d".r,
    "ugmcantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "undip.cantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r
  )

  def flow: Future[JsValue] = {
    Future.sequence(instagramAccounts.map { account =>
      val accountName = account._1
      val accountRegex = account._2
      val fetchResult = getPage(accountName, None)
      val photoRepoFuture = photoRepo.getAll
      val customerRepoFuture = customerRepo.getAllSubscribedCust

      for {
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

  def getPage(account: String,
              lastId: Option[String] = None): InstagramUser = {

    val request = getInstagramPageRequest(account, lastId)
    val response = request.asString
    val instagramUser = response.body.parseJson.convertTo[InstagramUser]
    val tmpResult = instagramUser.nodes
    val lastIdRes = tmpResult.lastOption.map(_.id)
    val nextResult = if (tmpResult.nonEmpty)
      getPage(account, lastIdRes).nodes
    else
      Seq.empty

    instagramUser.copy(nodes = tmpResult ++ nextResult)
  }

}
