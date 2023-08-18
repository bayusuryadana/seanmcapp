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
  shares: Double,
  liability: Int,
  equity: Int,
  netProfitCurrentYear: Int,
  netProfitPrevYear: Int,
  ///////////////////////////
  eipBestBuy: Option[Int],
  eipRating: Option[String],
  eipRisks: Option[String]
) {
  def PER(q: Int): Double = currentPrice / EPS / q
  val PBV: Double = marketCap.toDouble / equity
  val DER: Double = liability.toDouble / equity
  def ROE(q: Int): Double = netProfitCurrentYear.toDouble / equity * 100 * q
  val EPS: Double = netProfitCurrentYear / shares
  val marketCap: Int = (shares * currentPrice).toInt
  val profitChange: Double = (netProfitCurrentYear.toDouble / netProfitPrevYear - 1) * 100
}
// $COVERAGE-ON$

class StockInfo(tag: Tag) extends Table[Stock](tag, "stocks") {
  val id = column[String]("id", O.PrimaryKey)
  val currentPrice = column[Int]("current_price")
  val share = column[Double]("share")
  val liability = column[Int]("liability")
  val equity = column[Int]("equity")
  val netProfitCurrentYear = column[Int]("net_profit_current_year")
  val netProfitPrevYear = column[Int]("net_profit_previous_year")
  val eipBestBuy = column[Option[Int]]("eip_best_buy")
  val eipRating = column[Option[String]]("eip_rating")
  val eipRisks = column[Option[String]]("eip_risks")

  def * = (id, currentPrice, share, liability, equity, netProfitCurrentYear, netProfitPrevYear, 
    eipBestBuy, eipRating, eipRisks) <> (Stock.tupled, Stock.unapply)
}

trait StockRepo {

  def getAll(): Future[Seq[Stock]]

  def insert(stocks: Seq[Stock]): Future[Option[Int]]

  def update(stock: Stock): Future[Int]

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
  def update(stock: Stock): Future[Int] = {
      run(this.filter(_.id === stock.id).update(stock))
  }

}