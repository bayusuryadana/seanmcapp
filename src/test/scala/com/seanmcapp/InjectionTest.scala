package com.seanmcapp

import com.seanmcapp.mock.repository._
import com.seanmcapp.mock.requestbuilder.{DotaRequestBuilderMock, TelegramRequestBuilderMock}
import com.seanmcapp.repository.dota.{HeroRepo, PlayerRepo}
import com.seanmcapp.service._

trait CBCServiceImpl extends CBCService with TelegramRequestBuilderMock {
  override val photoRepo = PhotoRepoMock
  override val customerRepo = CustomerRepoMock
}

trait DotaServiceImpl extends DotaService with DotaRequestBuilderMock {
  override val playerRepo: PlayerRepo = PlayerRepoMock
  override val heroRepo: HeroRepo = HeroRepoMock
}
