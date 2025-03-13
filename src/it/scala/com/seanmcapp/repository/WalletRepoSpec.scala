package com.seanmcapp.repository

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class WalletRepoSpec extends AsyncWordSpec with Matchers {

//  "should do a CRUD in a wallet table" in {
//    val res1 = Await.result(WalletRepoImpl.getAll, Duration.Inf)
//    res1.size shouldBe 0
//    val wallet = Wallet(0, 201909, "Japan 2020", "Travel", "SGD", 100, false, "DBS")
//    Await.result(WalletRepoImpl.insert(wallet), Duration.Inf)
//    val res2 = Await.result(WalletRepoImpl.getAll, Duration.Inf)
//    res2.size shouldBe 1
//    val wallet2 = res2.head.copy(amount = 200)
//    Await.result(WalletRepoImpl.update(wallet2), Duration.Inf)
//    val res3 = Await.result(WalletRepoImpl.getAll, Duration.Inf)
//    res3.size shouldBe 1
//    res3.head.amount shouldBe 200
//    Await.result(WalletRepoImpl.delete(res3.head.id), Duration.Inf)
//    val res4 = Await.result(WalletRepoImpl.getAll, Duration.Inf)
//    res4.size shouldBe 0
//  }

}
