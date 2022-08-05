package com.seanmcapp.repository

import com.seanmcapp.repository.instagram.{AccountGroupTypes, AccountRepoImpl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class AccountRepoSpec extends AsyncWordSpec with Matchers {
  
  "getAll should return all value in the table with filter" in {
    val resF = AccountRepoImpl.getAll(AccountGroupTypes.StalkerSpecial)
    for {
      res <- resF
    } yield {
      res.head.alias shouldBe "xyyoff"
    }
  }

  "getAll should return nothing from the table" in {
    val resF = AccountRepoImpl.getAll(AccountGroupTypes.Unknown)
    for {
      res <- resF
    } yield {
      res.length shouldBe 0
    }
  }

}
