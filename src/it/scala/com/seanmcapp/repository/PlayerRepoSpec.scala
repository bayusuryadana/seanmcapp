package com.seanmcapp.repository

import com.seanmcapp.repository.dota.{Player, PlayerRepoImpl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class PlayerRepoSpec extends AsyncWordSpec with Matchers {

  "should return all player" in {
    val response = PlayerRepoImpl.getAll
    response.map { res =>
      res.size shouldEqual 11
      res.sortBy(_.id).headOption.map(_.personaName) shouldEqual Some("hnymnky")
    }
  }

  "should return successful insertion" in {
    val player = Player(105742997, "Bayu Suryadana",	"https://someurl",	"SeanmcrayZ", Some(35))
    val response = PlayerRepoImpl.update(player)
    response.map { res =>
      res shouldEqual 1
    }
  }

}
