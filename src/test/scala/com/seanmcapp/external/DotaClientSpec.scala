package com.seanmcapp.external

import com.seanmcapp.repository.dota.{HeroAttribute, Player}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class DotaClientSpec extends AnyWordSpec with Matchers {

  val http = Mockito.mock(classOf[HttpRequestClient])
  val dotaClient = new DotaClient(http)

  "getMatches" in {
    val playerId = "105742997"
    val mockResponse = Source.fromResource(s"matches/$playerId.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(mockResponse)
    val player = Player(playerId.toInt, "", "", "", None)
    val result = dotaClient.getMatches(player)
    val expected = List(
      MatchResponse(4829477839L, 130, true, 1272, 22, 26, 1560212867, 5, 5, 2),
      MatchResponse(4824100132L, 129, true, 2480, 22, 26, 1560047832, 8, 11, 10)
    )
    result shouldBe expected
  }

  "getPeers" in {
    val playerId = 131673450
    val mockResponse = Source.fromResource(s"peers/$playerId.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(mockResponse)
    val result = dotaClient.getPeers(playerId)
    val expected = List(
      PeerResponse(104466002,236,470),
      PeerResponse(137382742,229,460),
      PeerResponse(149059659,225,427),
      PeerResponse(133805346,163,351),
      PeerResponse(149343279,162,317)
    )
    result shouldBe expected
  }

  "getHeroStatsAndAttr" in {
    val heroStatsMockResponse = Source.fromResource("dota/hero_stats.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(heroStatsMockResponse)
    val result = dotaClient.getHeroStatsAndAttr
    val expected = (
      List(HeroResponse(1, "Anti-Mage", "agi", "Melee", List("Carry", "Escape", "Nuker"),
        "/apps/dota2/images/heroes/antimage_full.png?","/apps/dota2/images/heroes/antimage_icon.png")),
      List(HeroAttribute(1,200,0.25,75,0.0,-1,25,29,33,23,24,12,1.3,3.0,1.8,150,0,1.4,310,0.5,true))
    )
    result shouldBe expected
  }

  "getHeroLore" in {
    val result = dotaClient.getHeroLore
    val expected = Map(
      "bloodseeker" -> "bloodseeker lore",
      "bane" -> "bane lore",
      "antimage" -> "antimage lore",
      "axe" -> "axe lore",
      "crystal_maiden" -> "crystal_maiden"
    )
    result shouldBe expected
  }

  "getPlayerDetail" in {
    val playerId = "105742997"
    val player = Player(playerId.toInt, "", "", "", None)
    val mockResponse = Source.fromResource("dota/player_" + player.id + ".json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(mockResponse)
    val result = dotaClient.getPlayerDetail(player)
    val expected = PlayerResponse(ProfileResponse("SeanmcrayZ", "https://steamcdn-a.akamaihd.net/some_avatar_full.jpg"),
      Some(54))
    result shouldBe expected
  }

}
