package com.seanmcapp.service

import com.seanmcapp.external.TelegramClient
import org.mockito.Mockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SportsServiceSpec extends AnyWordSpec with Matchers {
  
  "Scheduler" in {
    val telegramClient = Mockito.mock(classOf[TelegramClient])
    val sportsService = new SportsService(telegramClient)
    sportsService.run
  }

}
