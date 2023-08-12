package com.seanmcapp.repository.seanmcstock

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
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
  eipRisks: Option[String],
  ///////////////////////////
  empAvgPrice: Option[Int],
  empCurrentLot: Option[Int],
  ///////////////////////////
  myAvgPrice: Option[Int],
  myCurrentLot: Option[Int]
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
  val empAvgPrice = column[Option[Int]]("emp_avg_price")
  val empCurrentLot = column[Option[Int]]("emp_current_lot")
  val myAvgPrice = column[Option[Int]]("my_avg_price")
  val myCurrentLot = column[Option[Int]]("my_current_lot")

  def * = (id, currentPrice, PER, PBV, ROE, shares, liability, equity, netProfitCurrentYear,
    netProfitPrevYear, EPS, marketCap, profitChange, eipBestBuy, eipRating, eipRisks, empAvgPrice, empCurrentLot,
    myAvgPrice, myCurrentLot) <> (Stock.tupled, Stock.unapply)
}

trait StockRepo {
  // insert bulk dari EIP
  def insert(stocks: Seq[Stock]): Future[Option[Int]]

  // update account / EMP
  def update(stock: Stock): Future[Int]

  // update bulk untuk price harian
  def update(stocks: Seq[Stock]): Future[Option[Int]]

  //
  def getAll(): Future[Seq[Stock]]

}

object StockRepoImpl extends TableQuery(new StockInfo(_)) with StockRepo with DBComponent {

  def insert(stocks: Seq[Stock]): Future[Option[Int]] = ???

  def update(stock: Stock): Future[Int] = run(this.filter(_.id === stock.id).update(stock))

  def update(stocks: Seq[Stock]): Future[Option[Int]] = ???

  def getAll(): Future[Seq[Stock]] = run(this.result)

}