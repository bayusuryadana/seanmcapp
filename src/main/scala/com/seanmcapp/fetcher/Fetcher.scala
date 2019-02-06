package com.seanmcapp.fetcher

import com.seanmcapp.repository.dota.{MatchRepo, PeerRepo, PlayerRepo}
import com.seanmcapp.repository.instagram.{AccountRepo, PhotoRepo}

trait Fetcher {

  val accountRepo: AccountRepo
  val photoRepo: PhotoRepo
  val playerRepo: PlayerRepo
  val matchRepo: MatchRepo
  val peerRepo: PeerRepo

}
