package com.seanmcapp.service

import com.seanmcapp.repository.WalletRepoMock
import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.{WalletCommon, WalletOutput}
import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers
import spray.json._

class WalletServiceSpec extends AsyncWordSpec with Matchers with WalletCommon {

  private val secretKey = "team-secret"

  val walletService = new WalletService(WalletRepoMock) {
    override val SECRET_KEY = secretKey
  }

  "failed to authenticate should return error" in {
    walletService.getAll("i dont know the password").map { res =>
      res shouldBe WalletOutput(403, Some("Wrong secret key"), None, List.empty[Wallet])
    }
  }

  "getAll should return all the rows" in {
    walletService.getAll(secretKey).map { res =>
      res.row shouldBe Some(3)
      res.response.size shouldBe 3
    }
  }

  "insert should return the inserted object" in {
    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, false, "DBS")
    walletService.insert(wallet.toJson)(secretKey).map { res =>
      res shouldBe WalletOutput(200, None, Some(1), List(Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, false, "DBS")))
    }
  }

  "update should return number of updated object" in {
    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, true, "DBS")
    walletService.update(wallet.toJson)(secretKey).map { res =>
      res shouldBe WalletOutput(200, None, Some(1), List.empty[Wallet])
    }
  }

  "delete should return number of deleted object" in {
    walletService.delete(123)(secretKey).map { res =>
      res shouldBe WalletOutput(200, None, Some(1), List.empty[Wallet])
    }
  }

}
