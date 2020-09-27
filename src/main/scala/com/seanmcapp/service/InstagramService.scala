package com.seanmcapp.service

import java.net.URL

import com.seanmcapp.external.InstagramClient
import com.seanmcapp.repository.FileRepo
import com.seanmcapp.repository.instagram.{Photo, PhotoRepo}

import scala.concurrent.Future
import scala.util.matching.Regex
import scala.concurrent.ExecutionContext.Implicits.global

case class InstagramAccountResponse(logging_page_id: String)

case class InstagramRequestParameter(id: String, first: Int, after: Option[String])

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
    /** DISCONTINUED
      * ui.cantik	662
      * ub.cantik	517
      * unj.cantik	425
      * bidadari_ub	257
      *
      * STILL UPDATING
      * ugmcantik	781
      * cantik.its	217
      * unpad.geulis	1262
      * undip.cantik	833
      * uicantikreal	175
      */

    // existing
    "ugmcantik"    -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "undip.cantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "unpad.geulis" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "cantik.its"   -> ".+".r,
    "uicantikreal" -> ".*".r,
  )

  def fetch(sessionId: String): Future[Seq[Option[Int]]] = {
    println("sessionId: " + sessionId)
    for {
      photos <- photoRepo.getAll
      result <- process(sessionId, photos)
    } yield result
  }

  private def process(sessionId: String, photos: Seq[Photo]): Future[Seq[Option[Int]]] = {
    val idsSet = photos.map(_.id).toSet
    val sequenceResult = accountList.toSeq.map { item =>
      val account = item._1
      val id = instagramClient.getAccountResponse(account).logging_page_id.replace("profilePage_", "")
      println(s"fetching: $account with id $id")
      val fetchedPhotos = fetch(id, account, None, sessionId)
      println(s"fetched: ${fetchedPhotos.size}")
      val unFetchedPhotos = fetchedPhotos.filterNot(photo => idsSet.contains(photo.id))
      println(s"non-exists: ${unFetchedPhotos.size}")
      val filteredPhotos = unFetchedPhotos.collect(filteringNonRelatedImage(item._2))
      println(s"filtered by rule: ${filteredPhotos.size}")
      val savedPhotos = savingToStorage(filteredPhotos)
      println(s"saved photos to storage: ${savedPhotos.size}")
      val result = photoRepo.insert(savedPhotos).map { res =>
        println(s"saved photos to database: ${res.getOrElse(-1)}")
        res
      }
      result
    }

    println(s"fetching finished")
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

  private def fetch(userId: String, account: String, endCursor: Option[String], sessionId: String): Seq[Photo] = {
    val instagramResponse = instagramClient.getPhotos(userId, endCursor, sessionId)

    val instagramPageInfo = instagramResponse.data.user.edge_owner_to_timeline_media.page_info
    val photos = instagramResponse.data.user.edge_owner_to_timeline_media.edges.map(_.node).map { node =>
      Photo(node.id.toLong, node.thumbnail_src, node.taken_at_timestamp, node.edge_media_to_caption.edges.headOption.map(_.node.text).getOrElse(""), account)
    }

    val result = if (instagramPageInfo.has_next_page && instagramPageInfo.end_cursor.isDefined) {
      photos ++ fetch(userId, account, instagramPageInfo.end_cursor, sessionId)
    } else {
      photos
    }

    result
  }

}
