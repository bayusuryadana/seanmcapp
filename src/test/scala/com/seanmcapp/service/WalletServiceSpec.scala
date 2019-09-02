package com.seanmcapp.service

import com.seanmcapp.mock.repository.WalletRepoMock
import com.seanmcapp.repository.seanmcwallet.Wallet
import com.seanmcapp.util.parser.WalletCommon
import com.seanmcapp.util.parser.encoder.WalletOutput
import org.scalatest.{AsyncWordSpec, Matchers}
import spray.json._

class WalletServiceSpec extends AsyncWordSpec with Matchers with WalletCommon {

  private val secretKey = "team-secret"

  val walletService = new WalletService(WalletRepoMock) {
    override val SECRET_KEY = secretKey
  }

  "failed to authenticate should return error" in {
    walletService.getAll("i dont know the password").map { res =>
      res shouldBe WalletOutput(403, Some("Wrong secret key"), None, None)
    }
  }

  "getAll should return all the rows" in {
    walletService.getAll(secretKey).map { res =>
      res.response.map(_.size) shouldBe Some(3)
    }
  }

  "insert should return the inserted object" in {
    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, false)
    walletService.insert(wallet.toJson)(secretKey).map { res =>
      res shouldBe WalletOutput(200, None, Some(1), Some(Seq(wallet)))
    }
  }

  "update should return number of updated object" in {
    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, true)
    walletService.update(wallet.toJson)(secretKey).map { res =>
      res shouldBe WalletOutput(200, None, Some(1), None)
    }
  }

  "delete should return number of deleted object" in {
    walletService.delete(123)(secretKey).map { res =>
      res shouldBe WalletOutput(200, None, Some(1), None)
    }
  }

}
