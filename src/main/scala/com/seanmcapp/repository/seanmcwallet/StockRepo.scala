package com.seanmcapp.repository.seanmcwallet

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Stock(code: String, qty: Int, avg: Int, best_buy: Int, intrinsic: Int)

class StockInfo(tag: Tag) extends Table[Stock](tag, "stocks") {
  val id = column[String]("id", O.PrimaryKey, O.AutoInc)
  val qty = column[Int]("qty")
  val avg = column[Int]("avg")
  val best_buy = column[Int]("best_buy")
  val intrinsic = column[Int]("intrinsic")

  def * = (id, qty, avg, best_buy, intrinsic) <> (Stock.tupled, Stock.unapply)
}

trait StockRepo {
  def getAll(): Future[Seq[Stock]]
}

object StockRepoImpl extends TableQuery(new StockInfo(_)) with StockRepo with DBComponent {
  def getAll(): Future[Seq[Stock]] = run(this.result)
}
