package com.seanmcapp.service

import com.seanmcapp.external.{MatchResponse, PlayerResponse, ProfileResponse}
import com.seanmcapp.repository.dota.{Hero, HeroAttribute, Player}
import com.seanmcapp.repository.{HeroAttributeRepoMock, HeroRepoMock, PlayerRepoMock}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class DotaServiceSpec extends AsyncWordSpec with Matchers {

  val dotaClient = new DotaClientMock
  val dotaService = new DotaService(PlayerRepoMock, HeroRepoMock, HeroAttributeRepoMock, dotaClient) {
    override private[service] def getLastXDays(days: Int): Long = 1500000000
  }

  "should fetch correct response and transform response properly - Home endpoint" in {
    dotaService.getHomePageData.map { res =>
      val playerInfos = List(
        PlayerInfo(
          Player(105742997, "Bayu Suryadana", "https://someurl", "SeanmcrayZ", Some(35)),
          WinSummary(0, 2, 0.0, None),
          List(
            MatchResponse(4829477839L, 130, true, 1272, 22, 26, 1560212867, 5, 5, 2),
            MatchResponse(4824100132L, 129, true, 2480, 22, 26, 1560047832, 8, 11, 10)
          ),
          List(
            (
              Hero(26, "Lion", "int", "Ranged", "Support,Disabler,Nuker,Initiator", "lion_full.png", "lion_icon.png", ""),
              WinSummary(0, 2, 0.0, Some(0.0))
            )
          )
        ),
        PlayerInfo(
          Player(137382742, "Rahmat Rasyidi Hakim", "https://someurl", "kill", Some(45)),
          WinSummary(2, 2, 1.0, None),
          List(
            MatchResponse(4827077503L, 1, true, 2095, 22, 18, 1560129450, 7, 5, 12),
            MatchResponse(4824100132L, 132, false, 2230, 22, 18, 1560047832, 3, 4, 24)
          ),
          List(
            (
              Hero(18, "Sven", "str", "Melee", "Carry,Disabler,Initiator,Durable,Nuker", "sven_full.png", "sven_icon.png", ""),
              WinSummary(2, 2, 1.0, Some(0.53125))
            )
          )
        )
      )

      val heroInfos = List(
        HeroInfo(
          Hero(18, "Sven", "str", "Melee", "Carry,Disabler,Initiator,Durable,Nuker", "sven_full.png", "sven_icon.png", "sven story here"),
          HeroAttribute(18,	200,	0.0,	75,	0,	1,	25,	41,	43,	22,	21,	16,	3.2,	2,	1.3,	150,	0,	1.8,	315,	0.6,	true),
          List(
            (Player(137382742, "Rahmat Rasyidi Hakim", "https://someurl", "kill", Some(45)), WinSummary(2, 2, 1.0, Some(0.53125))),
            (Player(105742997, "Bayu Suryadana", "https://someurl", "SeanmcrayZ", Some(35)), WinSummary(0, 0, 0.0, Some(0.5)))
          )
        ),
        HeroInfo(
          Hero(26, "Lion", "int", "Ranged", "Support,Disabler,Nuker,Initiator", "lion_full.png", "lion_icon.png", "lion story here"),
          HeroAttribute(26,	200,	0.0,	75,	0,	-1,	25,	29,	35,	18,	15,	18,	2.2,	1.5,	3.5,	600,	900,	1.7,	290,	0.5,	true),
          List(
            (Player(137382742, "Rahmat Rasyidi Hakim", "https://someurl", "kill", Some(45)), WinSummary(0, 0, 0.0, Some(0.0))),
            (Player(105742997, "Bayu Suryadana", "https://someurl", "SeanmcrayZ", Some(35)), WinSummary(0, 2, 0.0, Some(0.0)))
          )
        )
      )
      
      val aggregateMatchInfos = List(
        (MatchResponse(4829477839L, 130, true, 1272, 22, 26, 1560212867, 5, 5, 2), List(
          Player(105742997, "Bayu Suryadana", "https://someurl", "SeanmcrayZ", Some(35))
        )),
        (MatchResponse(4827077503L, 1, true, 2095, 22, 18, 1560129450, 7, 5, 12), List(
          Player(137382742, "Rahmat Rasyidi Hakim", "https://someurl", "kill", Some(45))
        )), 
        (MatchResponse(4824100132L, 132, false, 2230, 22, 18, 1560047832, 3, 4, 24), List(
          Player(137382742, "Rahmat Rasyidi Hakim", "https://someurl", "kill", Some(45)), 
          Player(105742997, "Bayu Suryadana", "https://someurl", "SeanmcrayZ", Some(35))
        )),
      )

      val expected = HomePageResponse(playerInfos, heroInfos, aggregateMatchInfos)
      res shouldBe expected
    }
  }

  "Scheduler" in {
    val expectedPlayerResponse = List(
      PlayerResponse(ProfileResponse("kill", "https://steamcdn-a.akamaihd.net/some_avatar_full.jpg"), Some(45)),
      PlayerResponse(ProfileResponse("SeanmcrayZ", "https://steamcdn-a.akamaihd.net/some_avatar_full.jpg"), Some(54))
    )
    val expectedHeroResponse = List(Hero(1, "Anti-Mage", "agi", "Melee", "Carry,Escape,Nuker", "antimage_full.png", "antimage_icon.png", ""))

    val expectedHeroAttributeResponse = List(
      HeroAttribute(1,200,0.25,75,0.0,-1,25,29,33,23,24,12,1.3,3.0,1.8,150,0,1.4,310,0.5,true)
    )

    dotaService.run.map { res =>
      res._1 shouldBe expectedPlayerResponse
      res._2.map(hero => hero.copy(lore = "")) shouldBe expectedHeroResponse
      res._3 shouldBe expectedHeroAttributeResponse
    }
  }

}
