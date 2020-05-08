package com.seanmcapp.service

import com.seanmcapp.mock.repository.{HeroRepoMock, PlayerRepoMock}
import com.seanmcapp.mock.requestbuilder.DotaRequestBuilderMock
import com.seanmcapp.repository.dota.{Hero, Player}
import com.seanmcapp.util.parser.encoder._
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

class DotaServiceSpec extends AsyncWordSpec with Matchers {

  val dotaService = new DotaService(PlayerRepoMock, HeroRepoMock, HttpRequestBuilderImpl) with DotaRequestBuilderMock {
    override val MINIMUM_MATCHES = 1
  }

  "should fetch correct response and transform response properly - Home endpoint" in {
    dotaService.home.map { res =>
      val expected = HomePageResponse(
        List(
          MatchViewModel(
            4829477839L,
            List(
              MatchPlayer(
                Player(104466002,"Agung Putra Pasaribu","https://someurl","hnymnky",Some(55)),
                Hero(26, "Axe","str","Melee","Initiator,Durable,Disabler,Jungler","axe_full.png","axe_icon.png",""),
                5,5,2)
            ),
            "Ranked All Pick",
            "11-06-2019 07:27",
            "21:12",
            "Dire",
            "Lose"
          ),
          MatchViewModel(
            4827077503L,
            List(
              MatchPlayer(
                Player(131673450,"Faris Iqbal","https://someurl","OMEGALUL",Some(62)),
                Hero(18, "Anti-Mage","agi","Melee","Carry,Escape,Nuker","antimage_full.png","antimage_icon.png",""),
                7,5,12)
            ),
            "Ranked All Pick",
            "10-06-2019 08:17",
            "34:55",
            "Radiant",
            "Win"
          ),
          MatchViewModel(
            4824100132L,
            List(
              MatchPlayer(
                Player(104466002,"Agung Putra Pasaribu","https://someurl","hnymnky",Some(55)),
                Hero(26, "Axe","str","Melee","Initiator,Durable,Disabler,Jungler","axe_full.png","axe_icon.png",""),
                8,11,10),
              MatchPlayer(
                Player(131673450,"Faris Iqbal","https://someurl","OMEGALUL",Some(62)),
                Hero(18, "Anti-Mage","agi","Melee","Carry,Escape,Nuker","antimage_full.png","antimage_icon.png",""),
                3,4,24)
            ),
            "Ranked All Pick",
            "09-06-2019 09:37",
            "41:20",
            "Dire",
            "Lose"
          )
        ),
        List(
          Player(104466002,"Agung Putra Pasaribu","https://someurl","hnymnky",Some(55)),
          Player(131673450,"Faris Iqbal","https://someurl","OMEGALUL",Some(62))
        ),
        List(
          Hero(18, "Anti-Mage","agi","Melee","Carry,Escape,Nuker","antimage_full.png","antimage_icon.png",""),
          Hero(26, "Axe","str","Melee","Initiator,Durable,Disabler,Jungler","axe_full.png","axe_icon.png","")
        )
      )

      res shouldEqual expected
    }
  }

  "should fetch correct response and transform response properly - Player endpoint" in {
    dotaService.player(131673450).map { res =>
      val expected = PlayerPageResponse(
        Player(131673450,"Faris Iqbal","https://someurl","OMEGALUL",Some(62)),
        List(
          HeroWinSummary(Hero(18, "Anti-Mage","agi","Melee","Carry,Escape,Nuker","antimage_full.png","antimage_icon.png",""),2,2,1.0,0.8333333333333334)
        ),
        List(
          PlayerWinSummary(Player(104466002,"Agung Putra Pasaribu","https://someurl","hnymnky",Some(55)),236,470,0.5,0.49894068753670323)
        ),
        List(
          MatchViewModel(4827077503L, List(), "Ranked All Pick", "10-06-2019 08:17", "34:55", "Radiant", "Win"),
          MatchViewModel(4824100132L, List(), "Ranked All Pick", "09-06-2019 09:37", "37:10", "Dire", "Win")
        ),
        PlayerWinSummary(Player(131673450,"Faris Iqbal","https://someurl","OMEGALUL",Some(62)),2,0,1.0,0.0)
      )

      res shouldEqual expected
    }
  }

  "should fetch correct response and transform response properly - Hero endpoint" in {
    dotaService.hero(18).map { res =>
      val expected = HeroPageResponse(
        Some(Hero(18, "Anti-Mage","agi","Melee","Carry,Escape,Nuker","antimage_full.png","antimage_icon.png","magina story here")),
        List(
          PlayerWinSummary(Player(131673450,"Faris Iqbal","https://someurl","OMEGALUL",Some(62)),2,2,1.0,0.8333333333333334)
        )
      )

      res shouldEqual expected
    }
  }

}
