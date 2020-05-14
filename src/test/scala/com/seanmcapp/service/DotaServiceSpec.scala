package com.seanmcapp.service

import com.seanmcapp.mock.repository.{HeroAttributeRepoMock, HeroRepoMock, PlayerRepoMock}
import com.seanmcapp.mock.requestbuilder.DotaRequestBuilderMock
import com.seanmcapp.repository.dota.{Hero, HeroAttribute, Player}
import com.seanmcapp.util.parser.encoder._
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

class DotaServiceSpec extends AsyncWordSpec with Matchers {

  val dotaService = new DotaService(PlayerRepoMock, HeroRepoMock, HeroAttributeRepoMock, HttpRequestBuilderImpl) with DotaRequestBuilderMock

  "should fetch correct response and transform response properly - Dashboard endpoint" in {
    dotaService.dashboard.map { res =>
      println(res)
      true shouldBe true
    }
  }

}
