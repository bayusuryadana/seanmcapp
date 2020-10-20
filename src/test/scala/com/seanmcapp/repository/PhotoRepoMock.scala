package com.seanmcapp.repository

import com.seanmcapp.repository.instagram.{Photo, PhotoRepo}

import scala.concurrent.Future
import scala.util.Random

object PhotoRepoMock extends PhotoRepo {

  private val photoList = Seq(
    Photo(772020198343721705L, "https://someurl", 1406252001, "Ritha Amelia D. Psikologi'12", "ui.cantik"),
    Photo(990599194820723882L, "https://someurl", 1432308647, "Dwirika Widya. Hukum 2014", "ugmcantik"),
    Photo(956621307316650190L, "https://someurl", 1428258168, "Thia. Fisip 2012", "undip.cantik"),
    Photo(884893623514815734L, "https://someurl", 1419707561, "Delicia Gemma. Hukum 2011", "unpad.geulis"),
    Photo(784771576786862732L, "https://someurl", 1407772083, "Nadia Raissa. FISIP'13", "bidadari.ub")
  )

  override def getAll: Future[Seq[Photo]] = Future.successful(photoList)

  override def get(id: Long): Future[Option[Photo]] = Future.successful(photoList.find(_.id == id))

  override def getRandom: Future[Option[Photo]] = {
    val random = Random.nextInt()
    val result = photoList.sortBy(_ => random).headOption
    Future.successful(result)
  }

  override def insert(photos: Seq[Photo]): Future[Option[Int]] = Future.successful(Some(photos.size))

}
