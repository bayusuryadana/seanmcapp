package com.seanmcapp.repository

import anorm.{Macro, RowParser, SQL, SqlParser}

import scala.concurrent.Future

case class Wallet(id: Option[Int], date: Int, name: String, category: String, currency: String, amount: Int, done: Boolean, account: String)

trait WalletRepo {
  def getAll: Future[List[Wallet]]
  def insert(wallet: Wallet): Future[Int]
  def update(wallet: Wallet): Future[Int]
  def delete(id: Int): Future[Int]
}

class WalletRepoImpl(client: DatabaseClient) extends WalletRepo {

  private val walletParser: RowParser[Wallet] = Macro.namedParser[Wallet]

  def getAll: Future[List[Wallet]] = {
    client.withConnection { implicit connection =>
      SQL("SELECT * FROM wallets").as(walletParser.*)
    }
  }

  override def insert(wallet: Wallet): Future[Int] = {
    client.withConnection { implicit conn =>
      SQL("""
        INSERT INTO wallets (id, date, name, category, currency, amount, done, account)
        VALUES (DEFAULT, {date}, {name}, {category}, {currency}, {amount}, {done}, {account})
        RETURNING id
       """)
        .on(
          "date" -> wallet.date,
          "name" -> wallet.name,
          "category" -> wallet.category,
          "currency" -> wallet.currency,
          "currency" -> wallet.currency,
          "amount" -> wallet.amount,
          "done" -> wallet.done,
          "account" -> wallet.account,
        )
        .as(SqlParser.scalar[Int].single)
    }
  }

  override def update(wallet: Wallet): Future[Int] = {
    client.withConnection { implicit conn =>
      val result = SQL("""
        UPDATE wallets SET date={date}, name={name}, category={category}, currency={currency}, amount={amount}, done={done}, account={account} WHERE id = {id} RETURNING id
       """)
        .on(
          "id" -> wallet.id,
          "date" -> wallet.date,
          "name" -> wallet.name,
          "category" -> wallet.category,
          "currency" -> wallet.currency,
          "currency" -> wallet.currency,
          "amount" -> wallet.amount,
          "done" -> wallet.done,
          "account" -> wallet.account,
        )
        .as(SqlParser.scalar[Int].singleOpt)

      result match {
        case Some(id) => id
        case _ => -1
      }
    }
  }

  override def delete(id: Int): Future[Int] = client.withConnection { implicit conn =>
    val result = SQL("DELETE FROM wallets WHERE id={id} RETURNING id").on("id" -> id).as(SqlParser.scalar[Int].singleOpt)
    result match {
      case Some(id) => id
      case _ => -1
    }
  }
}