package com.seanmcapp.service

import com.seanmcapp.repository.WalletRepoMock
import com.seanmcapp.repository.seanmcwallet.Wallet
import org.mockito.Mockito
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class WalletServiceSpec extends AsyncWordSpec with Matchers {

  private val secretKey = "team-secret"

  val amarthaServiceMock = Mockito.mock(classOf[AmarthaService])
  val stockServiceMock = Mockito.mock(classOf[StockService])
  val walletService = new WalletService(WalletRepoMock, amarthaServiceMock, stockServiceMock) {
    override val SECRET_KEY = secretKey
  }

  "insert should return the inserted object" in {
    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, false, "DBS")
    val res = walletService.insert(wallet)(secretKey)
    res shouldBe 1
  }

  "update should return number of updated object" in {
    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, true, "DBS")
    val res = walletService.update(wallet)(secretKey)
    res shouldBe 1
  }

  "delete should return number of deleted object" in {
    val res = walletService.delete(123)(secretKey)
    res shouldBe 1
  }

}
