package com.seanmcapp.service

import com.seanmcapp.external.StockClient
import com.seanmcapp.repository.seanmcwallet.{Stock, StockRepo}

import scala.collection.SortedMap
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class StockData(ticks: List[Int], internalData: Stock)

class StockService(stockClient: StockClient, stockRepo: StockRepo) {

  def getStock(): Future[SortedMap[String, StockData]] = {
    val stockDataF = stockRepo.getAll()

    for {
      stockData <- stockDataF
    } yield {
      val stockResult = stockClient.getStockResult(stockData.map(_.code))
      stockResult.result.toSeq.map { case (id, detail) =>
        val code = id.replace(":IJ", "")
        val ticks = detail.ticks.map(_.close)
        val internalData = stockData.find(_.code == code).getOrElse(throw new Exception("Stock internal data not found"))
        code -> StockData(ticks, internalData)
      }.sortBy(_._1).to(SortedMap)
    }
  }

}
