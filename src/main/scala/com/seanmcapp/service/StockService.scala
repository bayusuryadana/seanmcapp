package com.seanmcapp.service

import com.github.tototoshi.csv.CSVReader
import com.seanmcapp.external.StockClient
import com.seanmcapp.repository.seanmcstock.{Stock, StockRepo}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class StockService(stockRepo:StockRepo, stockClient: StockClient) extends ScheduledTask {
  override def run(): Future[Seq[Int]] = {
    val result = for {
      stocks <- stockRepo.getAll()
    } yield {
      val updatedStocks = stocks.map { stock =>
        Thread.sleep(1000)
        stockRepo.update(stock.copy(currentPrice = stockClient.fetchCurrentPrice(stock.id)))
      }
      Future.sequence(updatedStocks)
    }
    
    result.flatten
  }
  
  def refresh(): Future[Option[Int]] = {
    val file = Source.fromResource("kalkulator_saham.csv")
    val csvReader = CSVReader.open(file).all()
    
    val eipMap = Source.fromResource("eip.txt").getLines().toSeq.map { row =>
      val columns = row.split(" ").toSeq
      columns.head -> columns
    }.toMap
    
    val inputData = csvReader.map { row =>
      val code = row.head
      val (bestBuy, rating, risks) = eipMap.get(code).map(o => (o(1).toInt, o(2), o(3))).unzip3
      Stock(
        code,
        row(1).replace(",", "").toInt,
        row(8).replace(",", "").toDouble,
        row(9).replace(",", "").toInt,
        row(10).replace(",", "").toInt,
        row(11).replace(",", "").toInt,
        row(12).replace(",", "").toInt,
        bestBuy,
        rating,
        risks
      )
    }
    
    stockRepo.insert(inputData)
  }
}
