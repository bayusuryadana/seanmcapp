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
  private val instagramAccounts = Map(
    "ui.cantik" -> "[\\w]+'\\d\\d".r,
    "ugmcantik" -> "\\d\\d\\d\\d\\n#ugmcantik".r
  )

  def flow: Future[InstagramUser] = {

    val auth = None //InstagramService.getAuth(instagramConf.username, instagramConf.password)
    val fetchResult = getPage("ui.cantik", None)
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