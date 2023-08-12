package com.seanmcapp.repository.seanmcstock

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

case class StockPortfolio(
  id: String,
  currentPrice: Int,
  empAvgPrice: Option[Int],
  empCurrentLot: Option[Int],
  ///////////////////////////
  myAvgPrice: Option[Int],
  myCurrentLot: Option[Int],
)

class StockPortfolioInfo(tag: Tag) extends Table[StockPortfolio](tag, "stock_portfolios") {
  val id = column[String]("id", O.PrimaryKey)
  val currentPrice = column[Int]("current_price")
  val empAvgPrice = column[Option[Int]]("emp_avg_price")
  val empCurrentLot = column[Option[Int]]("emp_current_lot")
  val myAvgPrice = column[Option[Int]]("my_avg_price")
  val myCurrentLot = column[Option[Int]]("my_current_lot")

  def * = (id, currentPrice, empAvgPrice, empCurrentLot,
    myAvgPrice, myCurrentLot)<> (StockPortfolio.tupled, StockPortfolio.unapply)
}

trait StockPortfolioRepo {

  def getAll(): Future[Seq[StockPortfolio]]

  def insert(stockPortfolio: StockPortfolio): Future[Int]

  def update(stockPortfolio: StockPortfolio): Future[Int]

  def delete(id: String): Future[Int]

}

object StockPortfolioImpl extends TableQuery(new StockPortfolioInfo(_)) with StockPortfolioRepo with DBComponent {

  def getAll(): Future[Seq[StockPortfolio]] = run(this.result)

  def insert(stockPortfolio: StockPortfolio): Future[Int] = {
    val insertQuery = this.returning(this.map(_.id)).into((item, id) => item.copy(id = id))
    run((insertQuery += stockPortfolio).asTry).map {
      case Failure(_) => 0 // TODO: need better handle
      case Success(_) => 1
    }
  }

  def update(stockPortfolio: StockPortfolio): Future[Int] =
    run(this.filter(_.id === stockPortfolio.id).update(stockPortfolio))

  def delete(id: String): Future[Int] = run(this.filter(_.id === id).delete)
}
