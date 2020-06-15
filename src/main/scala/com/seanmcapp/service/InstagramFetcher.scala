package com.seanmcapp.service

import java.net.URL

import com.seanmcapp.repository.instagram.{Photo, PhotoRepo}
import com.seanmcapp.storage.ImageStorage
import com.seanmcapp.util.parser.decoder.{InstagramAccountResponse, InstagramDecoder, InstagramResponse}
import com.seanmcapp.util.requestbuilder.{HeaderMap, HttpRequestBuilder}

import scala.concurrent.Future
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global

class InstagramFetcher(photoRepo: PhotoRepo, imageStorage: ImageStorage, http: HttpRequestBuilder) extends InstagramDecoder {

  private[service] val accountList = Map(
    /**
      * ui.cantik	662
      * ub.cantik	517
      *
      * ugmcantik	781
      * cantik.its	217
      * unj.cantik	425
      * unpad.geulis	1262
      * undip.cantik	833
      *
      * bidadari_ub	257
      * uicantikreal	175
      */

    // deprecated
    //"ui.cantik"    -> "[\\w ]+\\. [\\w ]+['’]\\d\\d".r,
    //"ub.cantik"    -> "[\\w ]+\\. [\\w ]+['’]\\d\\d".r,
    //"unj.cantik"   -> "[\\w ]+\\, [\\w]+ ['’]\\d\\d".r,

    // existing
    "ugmcantik"    -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "undip.cantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "unpad.geulis" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "cantik.its"   -> ".+".r,

    // TODO: should use another function than regex
    // new
    "bidadari_ub"  -> ".*".r,
    "uicantikreal" -> ".*".r,
  )

  def fetch(cookie: String): Future[Seq[Option[Int]]] = {
    println("cookie: " + cookie)
    for {
      photos <- photoRepo.getAll
      result <- process(cookie, photos)
    } yield result
  }

  private def process(cookie: String, photos: Seq[Photo]): Future[Seq[Option[Int]]] = {
    val idsSet = photos.map(_.id).toSet
    val sequenceResult = accountList.toSeq.map { item =>
      val account = item._1
      val initUrl = "https://www.instagram.com/" + account + "/?__a=1"
      val headers = Some(HeaderMap(Map("cookie" -> cookie)))
      val httpResponse = http.sendRequest(initUrl, headers = headers)
      val id = decode[InstagramAccountResponse](httpResponse).id.replace("profilePage_", "").toLong

      /**
        * based on this answer https://stackoverflow.com/questions/49265339/instagram-a-1-url-not-working-anymore-problems-with-graphql-query-to-get-da
        * you can use either:
        * query_id=17888483320059182 OR query_hash=472f257a40c653c64c666ce877d59d2b
        */
      println("fetching: " + account)
      val fetchedPhotos = fetch(id, None, account, cookie)
      println("fetched: " + fetchedPhotos.size)
      val nonFetchedPhotos = fetchedPhotos.filterNot(photo => idsSet.contains(photo.id))
      println("non-exists: " + nonFetchedPhotos.size)
      val filteredPhotos = nonFetchedPhotos.collect(filteringNonRelatedImage(item._2))
      println("filtered by rule: " + filteredPhotos.size)
      val savedPhotos = savingToStorage(filteredPhotos)
      println("saved photos to storage: " + savedPhotos.size)
      val result = photoRepo.insert(savedPhotos).map { res =>
        println("saved photos to database: " + res.getOrElse(-1))
        res
      }
      result
    }

    Future.sequence(sequenceResult)
  }

  private[service] def savingToStorage(filteredPhotos: Seq[Photo]): Seq[Photo] = {
    filteredPhotos.flatMap { photo =>
      val inputStream = new URL(photo.thumbnailSrc).openStream
      imageStorage.put(photo.id  + ".jpg", inputStream).map(_ => photo)
    }
  }

  private def filteringNonRelatedImage(regex: Regex) = new PartialFunction[Photo, Photo] {

    def apply(photo: Photo): Photo = {
      val caption = regex.findFirstIn(photo.caption).getOrElse(throw new Exception("caption suddenly not found"))
      photo.copy(caption = caption.take(100))
    }

    override def isDefinedAt(photo: Photo): Boolean = regex.findFirstIn(photo.caption).isDefined

  }

  private def fetch(userId: Long, endCursor: Option[String], account: String, cookie: String): Seq[Photo] = {
    val fetchUrl = "https://www.instagram.com/graphql/query/?query_id=17888483320059182&id=<user_id>&first=50&after=<end_cursor>"
    val url = fetchUrl.replace("<user_id>", userId.toString).replace("<end_cursor>", endCursor.getOrElse(""))
    val headers = Some(HeaderMap(Map("cookie" -> cookie)))
    val httpResponse = http.sendRequest(url, headers = headers)
    val instagramResponse = decode[InstagramResponse](httpResponse)

    val instagramPageInfo = instagramResponse.graphql.user.media.pageInfo
    val photos = instagramResponse.graphql.user.media.edges.map(_.node).map { node =>
      Photo(node.id.toLong, node.thumbnailSrc, node.date, node.caption.edges.headOption.map(_.node.text).getOrElse(""), account)
    }

    val result = if (instagramPageInfo.hasNextPage && instagramPageInfo.endCursor.isDefined) {
      photos ++ fetch(userId, instagramPageInfo.endCursor, account, cookie)
    } else {
      photos
    }

    result
  }

}
