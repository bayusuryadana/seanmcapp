package com.seanmcapp.service

import com.seanmcapp.external.AmarthaView
import com.seanmcapp.repository.WalletRepoMock
import com.seanmcapp.repository.seanmcwallet.Wallet
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class WalletServiceSpec extends AsyncWordSpec with Matchers {

  private val secretKey = "team-secret"

  val amarthaServiceMock = Mockito.mock(classOf[AmarthaService])
  val walletService = new WalletService(WalletRepoMock, amarthaServiceMock) {
    override val SECRET_KEY = secretKey
  }

  "dashboard should return correct data" in {
    val dashboardView = walletService.dashboard(secretKey)
    dashboardView.savingAccount.get("SGD") shouldBe Some("1,300")
    dashboardView.chart.expense("SGD") shouldBe Map(
      "Fashion" -> List(0),
      "Zakat" -> List(0),
      "Misc" -> List(0),
      "Travel" -> List(850),
      "Rent" -> List(700),
      "Funding" -> List(0),
      "Daily" -> List(745),
      "IT Stuff" -> List(0),
      "Wellness" -> List(0)
    )
  }

  "data should return correct data" in {
    val dataView = walletService.data(secretKey, None)
    dataView.sgdBalance shouldBe Balance("-295", "-295", "-295")
    dataView.idrBalance shouldBe Balance("0","0","0")
  }

  "amartha should return amarthaView" in {
    val responseMock = Mockito.mock(classOf[AmarthaView])
    when(amarthaServiceMock.getAmarthaView()).thenReturn(responseMock)
    walletService.amartha(secretKey)
    verify(amarthaServiceMock, times(1)).getAmarthaView()
    succeed
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

  "getMonth should return month name respectively" in {
    val i = List(1,2,3,4,5,6,7,8,9,10,11,12)
    val expected = List("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    i.map(walletService.getMonth) shouldBe expected
  }

}
