package com.seanmcapp.external

import java.util.concurrent.TimeUnit

import com.seanmcapp.repository.dota.{HeroAttribute, Player}
import com.seanmcapp.util.MemoryCache
import scalacache.Cache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.io.{Codec, Source}

class DotaClient(http: HttpRequestClient) extends MemoryCache {

  private[external] val dotaBaseUrl = "https://api.opendota.com/api/players/"
  private[external] val dotaHeroStatsUrl = "https://api.opendota.com/api/herostats"

  implicit val matchesCache: Cache[Seq[MatchResponse]] = createCache[Seq[MatchResponse]]
  implicit val peersCache: Cache[Seq[PeerResponse]] = createCache[Seq[PeerResponse]]

  val duration: FiniteDuration = Duration(2, TimeUnit.HOURS)

  def getMatches(player: Player): Seq[MatchResponse] = {
    memoizeSync(Some(duration)) {
      val response = http.sendGetRequest(dotaBaseUrl + player.id + "/matches")
      decode[Seq[MatchResponse]](response)
    }
  }

  def getPeers(id: Int): Seq[PeerResponse] = {
    memoizeSync(Some(duration)) {
      val response = http.sendGetRequest(dotaBaseUrl + id + "/peers")
      decode[Seq[PeerResponse]](response)
    }
  }

  def getHeroStatsAndAttr: (Seq[HeroResponse], Seq[HeroAttribute]) = {
    val heroStatsResponse = http.sendGetRequest(dotaHeroStatsUrl)
    val heroStats = decode[Seq[HeroResponse]](heroStatsResponse)
    val heroAttr = decode[Seq[HeroAttribute]](heroStatsResponse)
    (heroStats, heroAttr)
  }

  def getHeroLore: Map[String, String] = {
    val heroLoreResponse = try {
      Source.fromResource("dota/hero_lore.json")(Codec.UTF8).getLines().mkString
    } catch {
      case e: Throwable =>
        e.printStackTrace()
        ""
    }
    decode[Map[String, String]](heroLoreResponse)
  }

  def getPlayerDetail(player: Player): PlayerResponse = {
    val response = http.sendGetRequest(dotaBaseUrl + player.id)
    decode[PlayerResponse](response)
  }

}
