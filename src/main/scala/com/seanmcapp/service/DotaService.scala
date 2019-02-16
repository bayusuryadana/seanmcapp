package com.seanmcapp.service

import com.seanmcapp.repository.dota.{Hero, HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.PeerResponse
import com.seanmcapp.util.requestbuilder.DotaRequest
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DotaService extends DotaRequest {

  val playerRepo: PlayerRepo
  val heroRepo: HeroRepo

  def getRecentMatches: Future[JsValue] = {
    playerRepo.getAll.map { players =>
      getMatches(players.map(_.id)).sortBy(m => -(m._2.startTime + m._2.duration)).take(20).toJson
    }
  }

  def getHeroes: Future[JsValue] = heroRepo.getAll.map(_.toJson)

  def getHeroMatches(id: Int): Future[JsValue] = {
    val playerListF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      playerList <- playerListF
      hero <- heroF
    } yield {
      val heroInfo = hero.getOrElse(throw new Exception("Hero not found"))
      val matches = getMatches(playerList.map(_.id)).filter(_._2.heroId == id).sortBy(m => -(m._2.startTime + m._2.duration))
      (heroInfo, matches).toJson
    }
  }

  def getPlayers: Future[JsValue] = playerRepo.getAll.map(_.toJson)

  def getPlayerMatches(id: Int): Future[JsValue] = {
    playerRepo.getAll.map { players =>
      val playerInfo = players.find(_.id == id).getOrElse(throw new Exception("Player not found"))
      val matches = getMatches(id)
      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        val player = players.find(_.id == peer.peerPlayerId)
        if (player.isDefined) res :+ (player.get, peer) else res
      }
      (playerInfo, matches, peers).toJson
    }
  }

}
