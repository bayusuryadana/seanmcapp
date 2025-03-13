package com.seanmcapp.repository

import anorm.{Macro, RowParser, SqlStringInterpolation}

import scala.concurrent.Future

case class Wallet(id: Int, date: Int, name: String, category: String, currency: String, amount: Int, done: Boolean, account: String)

trait WalletRepo {
  def getAll: Future[Seq[Wallet]]
  def insert(wallet: Wallet): Future[Int]
  def update(wallet: Wallet): Future[Int]
  def delete(id: Int): Future[Int]
}

class WalletRepoImpl(client: DatabaseClient) extends WalletRepo {

  val parser: RowParser[Wallet] = Macro.namedParser[Wallet]

  override def getAll: Future[Seq[Wallet]] = client.withConnection { implicit conn =>
    SQL"SELECT name FROM users".as(parser.*)
  }

  override def insert(wallet: Wallet): Future[Int] = client.withConnection { implicit conn =>
    SQL"SELECT name FROM users".as(parser.*)
    1
  }
  override def update(wallet: Wallet): Future[Int] = client.withConnection { implicit conn =>
    SQL"SELECT name FROM users".as(parser.*)
    1
  }

  override def delete(id: Int): Future[Int] = client.withConnection { implicit conn =>
    SQL"SELECT name FROM users".as(parser.*)
    1
  }
}