package com.seanmcapp.repository.dota

import scala.concurrent.Future

case class Player(id: Int, realName: String, avatarFull: String, personaName: String, MMREstimate: Int)

trait PlayerRepo {

  def getAll: Future[Set[Int]]

}
