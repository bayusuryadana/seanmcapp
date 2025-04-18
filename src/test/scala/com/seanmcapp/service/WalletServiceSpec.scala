package com.seanmcapp.service

import com.seanmcapp.repository.WalletRepoMock
import com.seanmcapp.repository.Wallet
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class WalletServiceSpec extends AsyncWordSpec with Matchers {

  private val secretKey = "team-secret"

  val walletService = new WalletService(WalletRepoMock) {
    override val SECRET_KEY = secretKey
    override lazy val todayDate: Int = 201909
  }

  "dashboard should return correct data" in {
    val dashboardView = walletService.dashboard(secretKey)
    dashboardView.savingAccount.get("SGD") shouldBe Some("1,300")
    dashboardView.chart.ytdExpenses shouldBe Map(
      "Fashion" -> 0,
      "Zakat" -> 0,
      "Misc" -> 0,
      "Travel" -> 850,
      "Rent" -> 700,
      "Funding" -> 0,
      "Daily" -> 745,
      "IT Stuff" -> 0,
      "Wellness" -> 0
    )
  }

  "data should return correct data" in {
    val dataView = walletService.data(secretKey, None)
    dataView.sgdBalance shouldBe Balance("0", "-295", "1,300")
    dataView.idrBalance shouldBe Balance("0","0","0")
  }

  "getMonth should return month name respectively" in {
    val i = List(1,2,3,4,5,6,7,8,9,10,11,12)
    val expected = List("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
    i.map(walletService.getMonth) shouldBe expected
  }

  "parseInput should return correct wallet (done)" in {
    val fields = Map(
      "date" -> "202101",
      "id" -> "1",
      "name" -> "Condo rent",
      "category" -> "Rent",
      "currency" -> "SGD",
      "amount" -> "1000",
      "done" -> "on",
      "account" -> "DBS"
    )

    val expected = Wallet(1, 202101, "Condo rent", "Rent", "SGD", 1000, true, "DBS")
    walletService.parseInput(fields) shouldBe expected
  }

  "parseInput should return correct wallet" in {
    val fields = Map(
      "date" -> "202101",
      "id" -> "1",
      "name" -> "Condo rent",
      "category" -> "Rent",
      "currency" -> "SGD",
      "amount" -> "1000",
      "account" -> "DBS"
    )

    val expected = Wallet(1, 202101, "Condo rent", "Rent", "SGD", 1000, false, "DBS")
    walletService.parseInput(fields) shouldBe expected
  }

}
