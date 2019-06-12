package com.seanmcapp.mock.repository

import com.seanmcapp.repository.instagram.{Photo, PhotoRepo}

import scala.concurrent.Future
import scala.util.Random

object PhotoRepoMock extends PhotoRepo {

  private val photoList = Seq(
    Photo(772020198343721705L, "https://someurl", 1406252001, "Ritha Amelia D. Psikologi'12", "ui.cantik"),
    Photo(990599194820723882L, "https://someurl", 1432308647, "Dwirika Widya. Hukum 2014", "ugmcantik"),
    Photo(956621307316650190L, "https://someurl", 1428258168, "Thia. Fisip 2012", "undip.cantik"),
    Photo(884893623514815734L, "https://someurl", 1419707561, "Delicia Gemma. Hukum 2011", "unpad.geulis"),
    Photo(784771576786862732L, "https://someurl", 1407772083, "Nadia Raissa. FISIP'13", "ui.cantik"),
  )

  override def getAll(account: String): Future[Seq[(Long, Long)]] = {
    val result = photoList.filter(_.account == account).sortBy(p => -p.date).map(p => (p.id, p.date))
    Future.successful(result)
  }

  override def getLatest: Future[Option[Photo]] = Future.successful(photoList.sortBy(p => -p.date).headOption)

  override def getRandom(account: Option[String]): Future[Option[Photo]] = {
    val random = Random.nextInt()
    val filterResult = account match {
      case Some(acc) => photoList.filter(_.account == acc)
      case _ => photoList
    }
    val result = filterResult.sortBy(_ => random).headOption
    Future.successful(result)
  }

  override def insert(photos: Seq[Photo]): Future[Option[Int]] = Future.successful(Some(photos.size))
}
