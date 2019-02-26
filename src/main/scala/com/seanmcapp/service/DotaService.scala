package com.seanmcapp.service

import com.seanmcapp.repository.dota.{HeroRepo, Player, PlayerRepo}
import com.seanmcapp.util.parser.PeerResponse
import com.seanmcapp.util.requestbuilder.DotaRequest
import org.joda.time.DateTime

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DotaService extends DotaRequest {

  val playerRepo: PlayerRepo
  val heroRepo: HeroRepo

  def getRecentMatches: Future[String] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val playersMap = players.map(p => (p.id, p)).toMap
      val matches = getMatches(players.map(_.id)).sortBy(m => -m._2.startTime).take(20)
      val heroesMap = heroes.map(h => (h.id, h)).toMap
      matches.foldLeft("") { (res, matchTuple) =>
        val m = matchTuple._2
        res + s"${playersMap.get(matchTuple._1).map(_.personaName).getOrElse("Unknown Player")} :  ${m.matchId} " +
          s"${m.getSide}  ${m.getWinStatus} ${m.getDuration}  ${m.getGameMode}  " +
          s"${heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")}  " +
          s"${new DateTime(m.startTime.toLong * 1000)} " +
          s"${m.kills}/${m.deaths}/${m.assists} \n"
      }
    }
  }

  def getHeroes: Future[String] = heroRepo.getAll.map(_.foldLeft(""){ (res,h) =>
    res + s"${h.id} ${h.localizedName}  ${h.primaryAttr} \n"
  })

  def getHeroMatches(id: Int): Future[String] = {
    val playerListF = playerRepo.getAll
    val heroF = heroRepo.get(id)

    for {
      playerList <- playerListF
      hero <- heroF
    } yield {
      val heroInfo = hero.getOrElse(throw new Exception("Hero not found"))
      val matches = getMatches(playerList.map(_.id)).filter(_._2.heroId == id).sortBy(m => -(m._2.startTime + m._2.duration))
      (heroInfo, matches).toString
    }
  }

  def getPlayers: Future[String] = playerRepo.getAll.map(_.foldLeft(""){ (res,h) =>
    res + s"${h.realName} ${h.personaName} ${h.MMREstimate} \n"
  })

  def getPlayerMatches(id: Int): Future[String] = {

    val playersF = playerRepo.getAll
    val heroesF = heroRepo.getAll

    for {
      players <- playersF
      heroes <- heroesF
    } yield {
      val playerInfo = players.find(_.id == id).getOrElse(throw new Exception("Player not found"))
      val matches = getMatches(id)
      val peers = getPeers(id).foldLeft(Seq.empty[(Player, PeerResponse)]) { (res, peer) =>
        val player = players.find(_.id == peer.peerPlayerId)
        if (player.isDefined) res :+ (player.get, peer) else res
      }

      val heroesMap = heroes.map(h => (h.id, h)).toMap
      val matchesS = matches.foldLeft("")( (res,m) => res + s"${m.matchId}  ${m.getSide}  ${m.getWinStatus} ${m.getDuration}  " +
        s"${m.getGameMode}  ${heroesMap.get(m.heroId).map(_.localizedName).getOrElse("Unknown")}  " +
        s"${new DateTime(m.startTime.toLong * 1000)} ${m.kills}/${m.deaths}/${m.assists} \n")

      val peersS = peers.foldLeft("")( (res,p) => res + s"${p._1.personaName} ${p._2.games} " +
        s"${((p._2.win.toDouble/p._2.games) * 100).toInt / 100.0} \n")

      s"""
        | Player Info
        | ===========
        | name:  ${playerInfo.realName}
        | alias: ${playerInfo.personaName}
        | mmr:   ${playerInfo.MMREstimate}
        |
        | Matches
        | =======
        | $matchesS
        |
        | Peers
        | =====
        | $peersS
      """.stripMargin

    }
  }

}
