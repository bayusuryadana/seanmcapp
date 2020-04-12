package com.seanmcapp.service

import com.seanmcapp.mock.repository.{HeroRepoMock, PlayerRepoMock}
import com.seanmcapp.mock.requestbuilder.DotaRequestBuilderMock
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl
import org.scalatest.{AsyncWordSpec, Matchers}

class DotaServiceSpec extends AsyncWordSpec with Matchers {

  val dotaService = new DotaService(PlayerRepoMock, HeroRepoMock, HttpRequestBuilderImpl) with DotaRequestBuilderMock {
    override val MINIMUM_MATCHES = 1
  }

  "should fetch correct response and transform response properly - Home endpoint" in {
    dotaService.home.map { res =>
      res.matches.size shouldEqual 10

      res.matches.flatMap(_.players).count(_.player.personaName == "SeanmcrayZ") shouldEqual 5
      res.matches.flatMap(_.players).count(_.player.personaName == "OMEGALUL") shouldEqual 5
      res.matches.flatMap(_.players).count(_.player.personaName == "lightzard") shouldEqual 2
      res.matches.flatMap(_.players).count(_.player.personaName == "travengers") shouldEqual 1
      res.matches.flatMap(_.players).count(_.player.personaName == "hnymnky") shouldEqual 2
      res.matches.headOption.map(_.players.size) shouldEqual Some(3)

      res.players.size shouldEqual 5
      res.heroes.size shouldEqual 5
    }
  }

  "should fetch correct response and transform response properly - Player endpoint" in {
    dotaService.player(105742997).map { res =>
      res.player.id shouldEqual 105742997
      res.player.realName shouldEqual "Bayu Suryadana"
      res.player.avatarFull shouldBe "https://someurl"
      res.player.personaName shouldEqual "SeanmcrayZ"

      res.heroes.size shouldEqual 9
      res.heroes.headOption.map(_.percentage) shouldEqual Some(1.0)

      res.peers.size shouldEqual 2
      res.peers.map(_.player.personaName) shouldBe List("hnymnky", "lightzard")
      res.peers.map(_.percentage) shouldBe List(0.5, 0.46)

    }
  }

  "should fetch correct response and transform response properly - Hero endpoint" in {
    dotaService.hero(4).map { res =>
      res.hero.map(_.id) shouldEqual Some(4)
      res.hero.map(_.localizedName) shouldEqual Some("Bloodseeker")
      res.hero.map(_.primaryAttr) shouldEqual Some("agi")
      res.hero.map(_.image) shouldEqual Some("bloodseeker_full.png")
      res.hero.map(_.lore) shouldEqual Some("strygwyr story here")

      res.players.size shouldEqual 1
      res.players.headOption.map(_.player.personaName) shouldBe Some("lightzard")
      res.players.headOption.map(_.percentage) shouldBe Some(0.5)
    }
  }

}
