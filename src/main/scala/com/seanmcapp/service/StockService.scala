package com.seanmcapp.service

import com.seanmcapp.external.StockClient

class StockService(stockClient: StockClient) {

  def getStock(): Map[String, List[Int]] = {
    val stockResult = stockClient.getStockResult()
    stockResult.result.toSeq.map { case (id, detail) =>
      id.replace(":IJ", "") -> detail.ticks.map(_.close)
    }.toMap
  }

}
