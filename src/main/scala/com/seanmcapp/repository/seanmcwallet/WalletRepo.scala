package com.seanmcapp.repository.seanmcwallet

import com.seanmcapp.repository.DBComponent
import io.circe.syntax._
import io.circe.Printer
import io.circe.generic.auto._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

// $COVERAGE-OFF$
case class Wallet(id: Int, date: Int, name: String, category: String, currency: String, amount: Int, done: Boolean, account: String) {
  def toJsonString(): String = this.asJson.printWith(Printer.noSpacesSortKeys)
}
// $COVERAGE-ON$

class WalletInfo(tag: Tag) extends Table[Wallet](tag, "wallets") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val date = column[Int]("date")
  val name = column[String]("name")
  val category = column[String]("category")
  val currency = column[String]("currency")
  val amount = column[Int]("amount")
  val done = column[Boolean]("done")
  val account = column[String]("account")

  def * = (id, date, name, category, currency, amount, done, account) <> (Wallet.tupled, Wallet.unapply)
}

trait WalletRepo {

  def getAll: Future[Seq[Wallet]]

  def insert(wallet: Wallet): Future[Int]

  def update(wallet: Wallet): Future[Int]

  def delete(id: Int): Future[Int]

}

object WalletRepoImpl extends TableQuery(new WalletInfo(_)) with WalletRepo with DBComponent {

  def getAll: Future[Seq[Wallet]] = run(this.result)

  def insert(wallet: Wallet): Future[Int] = {
    val insertQuery = this.returning(this.map(_.id)).into((item, id) => item.copy(id = id))
    run((insertQuery += wallet).asTry).map {
      case Failure(_) => 0 // TODO: need better handle
      case Success(_) => 1
    }
  }

  def update(wallet: Wallet): Future[Int] = run(this.filter(_.id === wallet.id).update(wallet))

  def delete(id: Int): Future[Int] = run(this.filter(_.id === id).delete)

}
