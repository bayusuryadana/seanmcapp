package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.repository.PlayerRepoMock
import com.seanmcapp.util.parser.decoder.{HeroAttributeResponse, HeroResponse, PlayerResponse, ProfileResponse}
import org.mockito.Mockito.when
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class DotaMetadataSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "DotaMetadataScheduler should return correctly" in {
    val dota = new DotaMetadataScheduler(startTime, interval, PlayerRepoMock, http)
    PlayerRepoMock.playersList.map { player =>
      val mockResponse = Source.fromResource("scheduler/dota/player_" + player.id + ".json").mkString
      when(http.sendRequest(dota.dotaBaseUrl + player.id)).thenReturn(mockResponse)
    }

    val heroStatsMockResponse = Source.fromResource("scheduler/dota/hero_stats.json").mkString
    when(http.sendRequest(dota.dotaHeroStatsUrl)).thenReturn(heroStatsMockResponse)

    val expectedPlayerResponse = List(
      PlayerResponse(ProfileResponse("hnymnky", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/d1/d145e4465a020a67d8bcdefb362dae8019d2af4f_full.jpg"),None),
      PlayerResponse(ProfileResponse("travengers", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/c0/c03e59d51d93b3df4c6ea5f49a246ef1ed2836e6_full.jpg"),Some(43)),
      PlayerResponse(ProfileResponse("SeanmcrayZ", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/88/88b1333d6f78f9426fb51141c6d5fa8254b6e798_full.jpg"),Some(54)),
      PlayerResponse(ProfileResponse("OMEGALUL", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/58/5899046336c4a14358467c331a9de24f6daded9f_full.jpg"),Some(67)),
      PlayerResponse(ProfileResponse("lightzard", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/d3/d36ccfdfde5f66d6a109e2252d94337471277cf4_full.jpg"),None)
    )
    val expectedHeroResponse = List(
      HeroResponse(1, "Anti-Mage", "agi", "Melee", List("Carry", "Escape", "Nuker"),"/apps/dota2/images/heroes/antimage_full.png?",
        "/apps/dota2/images/heroes/antimage_icon.png")
    )

    val expectedHeroAttributeResponse = List(
      HeroAttributeResponse(1,200,0.25,75,0.0,-1,25,29,33,23,24,12,1.3,3.0,1.8,150,0,1.4,310,0.5,true)
    )

    dota.task.map { res =>
      res shouldBe (expectedPlayerResponse, expectedHeroResponse, expectedHeroAttributeResponse)
    }
  }

}
