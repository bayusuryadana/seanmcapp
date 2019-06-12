package com.seanmcapp.service

import com.seanmcapp.DotaServiceImpl
import org.scalatest.{AsyncWordSpec, Matchers}

class DotaServiceSpec extends AsyncWordSpec with Matchers with DotaServiceImpl {

  "should fetch correct response and transform response properly - Home endpoint" in {
    home.map { res =>
      res.matches.size shouldEqual 10
      res.matches.flatMap(_.players).count(_.name == "SeanmcrayZ") shouldEqual 5
      res.matches.flatMap(_.players).count(_.name == "OMEGALUL") shouldEqual 1
      res.matches.flatMap(_.players).count(_.name == "lightzard") shouldEqual 2
      res.matches.flatMap(_.players).count(_.name == "travengers") shouldEqual 1
      res.matches.flatMap(_.players).count(_.name == "hnymnky") shouldEqual 2

      res.matches.head.players.size shouldEqual 2

      res.players.size shouldEqual 5
      res.heroes.size shouldEqual 5
    }
  }

  "should fetch correct response and transform response properly - Player endpoint" in {
    player(105742997).map { res =>
      res.player.id shouldEqual 105742997
      res.player.realName shouldEqual "Bayu Suryadana"
      res.player.avatarFull shouldBe null
      res.player.personaName shouldEqual "SeanmcrayZ"

      res.heroes.size shouldEqual 1
      res.heroes.head.percentage shouldEqual 0.7

      res.peers.size shouldEqual 2
      res.peers.map(_.peerName) shouldBe List("hnymnky", "lightzard")
      res.peers.map(_.percentage) shouldBe List(0.5, 0.46)

      res.heroes.size shouldEqual 1
    }
  }

  "should fetch correct response and transform response properly - Hero endpoint" in {
    hero(4).map { res =>
      res.hero.id shouldEqual 4
      res.hero.localizedName shouldEqual "Bloodseeker"
      res.hero.primaryAttr shouldEqual "agi"
      res.hero.image shouldEqual "bloodseeker_full.png"
      res.hero.lore shouldEqual "strygwyr story here"

      res.players.size shouldEqual 1
      res.players.head.peerName shouldEqual "lightzard"
      res.players.head.percentage shouldEqual 0.5
    }
  }

}
