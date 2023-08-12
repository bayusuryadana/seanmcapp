package com.seanmcapp.repository.seanmcstock

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
case class Stock(
  id: String,
  currentPrice: Int,
  PER: Double,
  PBV: Double,
  ROE: Double,
  shares: Double,
  liability: Int,
  equity: Int,
  netProfitCurrentYear: Int,
  netProfitPrevYear: Int,
  EPS: Double,
  marketCap: Int,
  profitChange: Double,
  ///////////////////////////
  eipBestBuy: Option[Int],
  eipRating: Option[String],
  eipRisks: Option[String]
)
// $COVERAGE-ON$

class StockInfo(tag: Tag) extends Table[Stock](tag, "stocks") {
  val id = column[String]("id", O.PrimaryKey)
  val currentPrice = column[Int]("current_price")
  val PER = column[Double]("PER")
  val PBV = column[Double]("PBV")
  val ROE = column[Double]("ROE")
  val shares = column[Double]("shares")
  val liability = column[Int]("liability")
  val equity = column[Int]("equity")
  val netProfitCurrentYear = column[Int]("net_profit_current_year")
  val netProfitPrevYear = column[Int]("net_profit_prev_year")
  val EPS = column[Double]("EPS")
  val marketCap = column[Int]("market_cap")
  val profitChange = column[Double]("profit_change")
  val eipBestBuy = column[Option[Int]]("eip_best_buy")
  val eipRating = column[Option[String]]("eip_rating")
  val eipRisks = column[Option[String]]("eip_risks")

  def * = (id, currentPrice, PER, PBV, ROE, shares, liability, equity, netProfitCurrentYear,
    netProfitPrevYear, EPS, marketCap, profitChange, eipBestBuy, eipRating, eipRisks) <> (Stock.tupled, Stock.unapply)
}

trait StockRepo {

  def getAll(): Future[Seq[Stock]]

  def insert(stocks: Seq[Stock]): Future[Option[Int]]

  def update(stocks: Seq[Stock]): Future[Seq[Int]]

}

object StockRepoImpl extends TableQuery(new StockInfo(_)) with StockRepo with DBComponent {

  def getAll(): Future[Seq[Stock]] = run(this.result)

  def insert(stocks: Seq[Stock]): Future[Option[Int]] = {
    run(this.delete).flatMap { _ =>
      run((this ++= stocks).asTry).map {
        case Failure(ex) => throw new Exception(ex.getMessage)
        case Success(value) => value
      }
    }
  }

  // can be change to single call only, depends on price API response
  def update(stocks: Seq[Stock]): Future[Seq[Int]] = {
    val updates = stocks.map { stock =>
      run(this.filter(_.id === stock.id).update(stock))
    }
    Future.sequence(updates)
  }

}