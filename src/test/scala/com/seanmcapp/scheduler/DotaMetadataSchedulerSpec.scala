package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.repository.{HeroAttributeRepoMock, HeroRepoMock, PlayerRepoMock}
import com.seanmcapp.repository.dota.{Hero, HeroAttribute}
import com.seanmcapp.util.parser.decoder.{PlayerResponse, ProfileResponse}
import org.mockito.Mockito.when
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class DotaMetadataSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "DotaMetadataScheduler should return correctly" in {
    val dota = new DotaMetadataScheduler(startTime, interval, PlayerRepoMock, HeroRepoMock, HeroAttributeRepoMock, http)
    PlayerRepoMock.playersList.map { player =>
      val mockResponse = Source.fromResource("scheduler/dota/player_" + player.id + ".json").mkString
      when(http.sendRequest(dota.dotaBaseUrl + player.id)).thenReturn(mockResponse)
    }

    val heroStatsMockResponse = Source.fromResource("scheduler/dota/hero_stats.json").mkString
    when(http.sendRequest(dota.dotaHeroStatsUrl)).thenReturn(heroStatsMockResponse)

    val heroLoreMockResponse = Source.fromResource("scheduler/dota/lore.json").mkString
    when(http.sendRequest(dota.dotaLoreUrl)).thenReturn(heroLoreMockResponse)

    val expectedPlayerResponse = List(
      PlayerResponse(ProfileResponse("kill", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/07/07cf34fa7d03bec441f074c4d2a230b469e5d2c1_full.jpg"), Some(45)),
      PlayerResponse(ProfileResponse("SeanmcrayZ", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/88/88b1333d6f78f9426fb51141c6d5fa8254b6e798_full.jpg"), Some(54))
    )
    val expectedHeroResponse = List(Hero(1, "Anti-Mage", "agi", "Melee", "Carry,Escape,Nuker", "antimage_full.png", "antimage_icon.png", ""))

    val expectedHeroAttributeResponse = List(
      HeroAttribute(1,200,0.25,75,0.0,-1,25,29,33,23,24,12,1.3,3.0,1.8,150,0,1.4,310,0.5,true)
    )

    dota.task.map { res =>
      res._1 shouldBe expectedPlayerResponse
      res._2.map(hero => hero.copy(lore = "")) shouldBe expectedHeroResponse
      res._3 shouldBe expectedHeroAttributeResponse
    }
  }

}
