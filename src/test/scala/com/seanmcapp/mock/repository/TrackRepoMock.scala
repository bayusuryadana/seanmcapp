package com.seanmcapp.mock.repository

import com.seanmcapp.repository.instagram.{Track, TrackRepo}

import scala.concurrent.Future

object TrackRepoMock extends TrackRepo {

  private val trackList = Seq(
    Track(-111546505, 772020198343721705L, 1548344427),
    Track(-209240150, 990599194820723882L, 1543342361),
    Track(26694991, 772020198343721705L, 1539947450),
    Track(26694991, 956621307316650190L, 1541427524),
    Track(123, 784771576786862732L, 1543779535)
  )

  override def getAll: Future[Seq[Track]] = Future.successful(trackList)

  override def insert(track: Track): Future[Int] = Future.successful(1)
}
