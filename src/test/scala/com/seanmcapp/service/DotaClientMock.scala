package com.seanmcapp.service

import com.seanmcapp.external.{DotaClient, HttpRequestClient}
import com.seanmcapp.repository.dota.{HeroAttribute, Player}
import org.mockito.Mockito

import scala.io.Source

class DotaClientMock extends DotaClient(Mockito.mock(classOf[HttpRequestClient])) {

  import com.seanmcapp.external._

  override def getMatches(player: Player): Seq[MatchResponse] = {
    val source = Source.fromResource("matches/" + player.id + ".json").mkString
    decode[Seq[MatchResponse]](source)
  }

  override def getPeers(id: Int): Seq[PeerResponse] = {
    val source = Source.fromResource("peers/" + id + ".json").mkString
    decode[Seq[PeerResponse]](source)
  }

  override def getHeroStatsAndAttr: (Seq[HeroResponse], Seq[HeroAttribute]) = {
    val heroStatsMockResponse = Source.fromResource("dota/hero_stats.json").mkString
    val heroStats = decode[Seq[HeroResponse]](heroStatsMockResponse)
    val heroAttr = decode[Seq[HeroAttribute]](heroStatsMockResponse)
    (heroStats, heroAttr)
  }

  override def getHeroLore: Map[String, String] = {
    val heroLoreMockResponse = Source.fromResource("dota/hero_lore.json").mkString
    decode[Map[String, String]](heroLoreMockResponse)
  }

  override def getPlayerDetail(player: Player): PlayerResponse = {
    val mockResponse = Source.fromResource("dota/player_" + player.id + ".json").mkString
    decode[PlayerResponse](mockResponse)
  }

}
