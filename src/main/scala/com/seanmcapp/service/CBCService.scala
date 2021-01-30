package com.seanmcapp.service

import com.seanmcapp.external.CBCClient
import com.seanmcapp.repository.instagram._
import com.seanmcapp.util.MemoryCache
import scalacache.Cache
import scalacache.modes.sync._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CBCService(photoRepo: PhotoRepo, customerRepo: CustomerRepo, cbcClient: CBCClient) extends MemoryCache {

  implicit val lastPhotoCache: Cache[Long] = createCache[Long]

  def random: Future[Option[Photo]] = photoRepo.getRandom

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

}
