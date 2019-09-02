package com.seanmcapp.repository.seanmcwallet

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

case class Wallet(id: Int, date: Int, name: String, category: String, currency: String, amount: Int, done: Boolean)

class WalletInfo(tag: Tag) extends Table[Wallet](tag, "wallets") {
  val id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  val date = column[Int]("date")
  val name = column[String]("name")
  val category = column[String]("category")
  val currency = column[String]("currency")
  val amount = column[Int]("amount")
  val done = column[Boolean]("done")

  def * = (id, date, name, category, currency, amount, done) <> (Wallet.tupled, Wallet.unapply)
}

trait WalletRepo {

  def getAll: Future[Seq[Wallet]]

  def insert(wallet: Wallet): Future[Wallet]

  def update(wallet: Wallet): Future[Int]

  def delete(id: Int): Future[Int]

}

object WalletRepoImpl extends TableQuery(new WalletInfo(_)) with WalletRepo with DBComponent {

  def getAll: Future[Seq[Wallet]] = run(this.result)

  def insert(wallet: Wallet): Future[Wallet] = {
    val insertQuery = this.returning(this.map(_.id)).into((item, id) => item.copy(id = id))
    run((insertQuery += wallet).asTry).map {
      case Failure(ex) => throw new Exception(ex.getMessage)
      case Success(value) => value
    }
  }

  def update(wallet: Wallet): Future[Int] = run(this.filter(_.id === wallet.id).update(wallet))

  def delete(id: Int): Future[Int] = run(this.filter(_.id === id).delete)

}
