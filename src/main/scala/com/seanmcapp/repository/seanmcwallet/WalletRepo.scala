package com.seanmcapp.repository.seanmcwallet

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Wallet(id: Int, date: Int, category: String, currency: String, amount: Int)

class WalletInfo(tag: Tag) extends Table[Wallet](tag, "wallets") {
  val id = column[Int]("id", O.PrimaryKey)
  val date = column[Int]("date")
  val category = column[String]("category")
  val currency = column[String]("currency")
  val amount = column[Int]("amount")

  def * = (id, date, category, currency, amount) <> (Wallet.tupled, Wallet.unapply)
}

trait WalletRepo {

  def getAll: Future[Seq[Wallet]]

  def insert(wallet: Wallet): Future[Int]

  def update(wallet: Wallet): Future[Int]

  def delete(id: Int): Future[Int]

}

object WalletRepoImpl extends TableQuery(new WalletInfo(_)) with WalletRepo with DBComponent {

  def getAll: Future[Seq[Wallet]] = run(this.result)

  def insert(wallet: Wallet): Future[Int] = run(this += wallet)

  def update(wallet: Wallet): Future[Int] = run(this.filter(_.id === wallet.id).update(wallet))

  def delete(id: Int): Future[Int] = run(this.filter(_.id === id).delete)

}
