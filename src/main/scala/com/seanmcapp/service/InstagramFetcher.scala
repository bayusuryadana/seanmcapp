package com.seanmcapp.service

import java.net.URL

import com.seanmcapp.repository.instagram.{Photo, PhotoRepo}
import com.seanmcapp.storage.ImageStorage
import com.seanmcapp.util.parser.{InstagramAccountResponse, InstagramResponse}
import scalaj.http.Http

import scala.concurrent.Future
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global
import spray.json._
import com.seanmcapp.util.parser.InstagramJson._

trait InstagramFetcher {

  val photoRepo: PhotoRepo
  val imageStorage: ImageStorage

  private val accountList = Map(
    // deprecated
    //"ui.cantik"    -> "[\\w ]+\\. [\\w ]+['’]\\d\\d".r,    // (n/a) -> 662
    //"ub.cantik"    -> "[\\w ]+\\. [\\w ]+['’]\\d\\d".r,    // 524 -> 517

    // existing
    "ugmcantik"    -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,  // 1185 -> 658
    "undip.cantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,  // 897 -> 706
    "unpad.geulis" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,  // 1059 -> 1105
    "unj.cantik"   -> "[\\w ]+\\, [\\w]+ ['’]\\d\\d".r,    // 435 -> 399
    "cantik.its"   -> ".+".r,  // 111 -> 111

    // TODO: should use another function than regex
    // new
    //"bidadari_ub"  -> "".r   // 214 -> 173 can use whole caption value (214, requested)
    //"uicantikreal" -> "".r,  // 100 -> 97 no proper regex :(
  )

  // TODO: refactor and add tests for fetch
  def fetch(cookie: String): Future[Unit] = {
    for {
      photos <- photoRepo.getAll
    } yield {
      println("cookie: " + cookie)
      val idsSet = photos.map(_.id).toSet
      accountList.foreach { item =>
        val account = item._1
        val initUrl = "https://www.instagram.com/" + account + "/?__a=1"
        val httpResponse = Http(initUrl).header("cookie", cookie).asString.body
        val id = httpResponse.parseJson.convertTo[InstagramAccountResponse].id.replace("profilePage_", "").toLong

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
        photoRepo.insert(savedPhotos).map(res => println("saved photos to database: " + res.getOrElse(-1)))
        account
      }
    }
  }

  private def savingToStorage(filteredPhotos: Seq[Photo]): Seq[Photo] = {
    filteredPhotos.flatMap { photo =>
      val inputStream = new URL(photo.thumbnailSrc).openStream
      imageStorage.put(photo.id  + ".jpg", inputStream).map(_ => photo)
    }
  }

  private def filteringNonRelatedImage(regex: Regex) = new PartialFunction[Photo, Photo] {

    def apply(photo: Photo) = {
      val caption = regex.findFirstIn(photo.caption).get // won't get exception because alr filtered
      photo.copy(caption = caption)
    }

    override def isDefinedAt(photo: Photo): Boolean = regex.findFirstIn(photo.caption).isDefined

  }

  private def fetch(userId: Long, endCursor: Option[String], account: String, cookie: String): Seq[Photo] = {
    val fetchUrl = "https://www.instagram.com/graphql/query/?query_id=17888483320059182&id=<user_id>&first=50&after=<end_cursor>"
    val httpResponse = Http(fetchUrl.replace("<user_id>", userId.toString).replace("<end_cursor>", endCursor.getOrElse(""))).header("cookie", cookie).asString.body
    val instagramResponse = httpResponse.parseJson.convertTo[InstagramResponse]

    val instagramPageInfo = instagramResponse.data.user.media.pageInfo
    val photos = instagramResponse.data.user.media.edges.map(_.node).map { node =>
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
