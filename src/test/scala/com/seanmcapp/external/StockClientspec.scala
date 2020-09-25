package com.seanmcapp.external

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class StockClientspec extends AnyWordSpec with Matchers {

  "getStockResult" in {
    val http = HttpRequestClientImpl
    val stockClient = new StockClient(http)
    stockClient.getStockResult()
  }

}
