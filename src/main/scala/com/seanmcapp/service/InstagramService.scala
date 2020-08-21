package com.seanmcapp.service

import java.net.URL

import com.seanmcapp.external.InstagramClient
import com.seanmcapp.repository.FileRepo
import com.seanmcapp.repository.instagram.{Photo, PhotoRepo}

import scala.concurrent.Future
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global

case class InstagramAccountResponse(logging_page_id: String)

case class InstagramResponse(data: InstagramData)
case class InstagramData(user: InstagramUser)
case class InstagramUser(edge_owner_to_timeline_media: InstagramMedia)
case class InstagramMedia(count: Int, page_info: InstagramPageInfo, edges: Seq[InstagramEdge])
case class InstagramPageInfo(has_next_page: Boolean, end_cursor: Option[String])
case class InstagramEdge(node: InstagramNode)
case class InstagramNode(id: String, thumbnail_src: String, taken_at_timestamp: Long, edge_media_to_caption: InstagramMediaCaption)
case class InstagramMediaCaption(edges: Seq[InstagramEdgeCaption])
case class InstagramEdgeCaption(node: InstagramCaption)
case class InstagramCaption(text: String)

class InstagramService(photoRepo: PhotoRepo, fileRepo: FileRepo, instagramClient: InstagramClient) {

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
      val id = instagramClient.getAccountResponse(account, cookie).logging_page_id.replace("profilePage_", "").toLong

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
      fileRepo.put(photo.id  + ".jpg", inputStream).map(_ => photo)
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
    val instagramResponse = instagramClient.getPhotos(userId, endCursor, cookie)

    val instagramPageInfo = instagramResponse.data.user.edge_owner_to_timeline_media.page_info
    val photos = instagramResponse.data.user.edge_owner_to_timeline_media.edges.map(_.node).map { node =>
      Photo(node.id.toLong, node.thumbnail_src, node.taken_at_timestamp, node.edge_media_to_caption.edges.headOption.map(_.node.text).getOrElse(""), account)
    }

    val result = if (instagramPageInfo.has_next_page && instagramPageInfo.end_cursor.isDefined) {
      photos ++ fetch(userId, instagramPageInfo.end_cursor, account, cookie)
    } else {
      photos
    }

    result
  }

}
