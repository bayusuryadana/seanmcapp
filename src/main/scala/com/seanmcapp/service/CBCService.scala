package com.seanmcapp.service

import java.net.URL

import com.seanmcapp.external.{CBCClient, InstagramClient, InstagramNode}
import com.seanmcapp.repository.FileRepo
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.MemoryCache
import scalacache.Cache
import scalacache.modes.sync._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.matching.Regex

class CBCService(photoRepo: PhotoRepo, customerRepo: CustomerRepo, fileRepo: FileRepo, cbcClient: CBCClient,
                 instagramClient: InstagramClient) extends MemoryCache with ScheduledTask {

  implicit val lastPhotoCache: Cache[Long] = createCache[Long]

  private[service] val accountList = Map(
    /** DISCONTINUED
      * ui.cantik	    662
      * ub.cantik	    517
      * bidadari_ub	  257
      *
      * STILL UPDATING (total: 6165 @ 06-05-2021)
      * *ugmcantik	    980
      * cantik.its	  396
      * *unpad.geulis	1510
      * *undip.cantik	1038
      * *uicantikreal	296
      * unj.cantik	  509
      */

    // existing
    "ugmcantik"    -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "undip.cantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "unpad.geulis" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r,
    "unj.cantik"   -> "[\\w ]+, [A-Z]+ \\d\\d\\d\\d\\.".r,
    "cantik.its"   -> ".+".r,
  )

  def cbcFlow(userId: Long, userFullName: String, `type`: String): Future[Option[Photo]] = {
    val photoF = `type` match {
      case "cbc" => photoRepo.getRandom
      case "recommendation" =>
        lastPhotoCache.get(userId).flatMap { lastPhotoId =>
          cbcClient.getRecommendation.get(lastPhotoId).map { recommendations =>
            val r = scala.util.Random
            val photoId = recommendations(r.nextInt(recommendations.length))
            photoRepo.get(photoId)
          }
        }.getOrElse(Future.successful(None))
      case _ => throw new Exception("flow type not recognized")
    }

    for {
      customerOpt <- customerRepo.get(userId)
      photoOpt <- photoF
    } yield {
      photoOpt.map { photo =>
        // Tracking customer
        customerOpt match {
          // TODO: test which one and one of them should being called
          case Some(customer) => customerRepo.update(Customer(userId, userFullName, customer.count + 1))
          case None => customerRepo.insert(Customer(userId, userFullName, 1))
        }

        // Set cache, logging and send photo
        lastPhotoCache.put(userId)(photo.id)
        photo
      }
    }
  }

  def getPhotoUrl(photoId: Long) = s"${cbcClient.storageConf.host}/${cbcClient.storageConf.bucket}/cbc/$photoId.jpg"

  override def run(): Future[Seq[Option[Int]]] = startFetching()

  def startFetching(sessionIdOpt: Option[String] = None): Future[Seq[Option[Int]]] = {
    val sessionId = sessionIdOpt.getOrElse(instagramClient.postLogin())
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
      val fetchedPhotos = instagramClient.getAllPosts(id, None, sessionId).map(node => convert(node, account))
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

  private def convert(node: InstagramNode, account: String): Photo =
    Photo(node.id.toLong, node.thumbnail_src, node.taken_at_timestamp,
      node.edge_media_to_caption.edges.headOption.map(_.node.text).getOrElse(""), account)
  
}
