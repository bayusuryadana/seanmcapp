package com.seanmcapp.repository

import anorm.{Macro, RowParser, SQL, SqlParser}

import scala.concurrent.Future

case class Wallet(id: Option[Int], date: Int, name: String, category: String, currency: String, amount: Int, done: Boolean, account: String)
case class Balance(date: Int, sum: Int)
case class Expenses(category: String, amount: Int)

trait WalletRepo {
  def insert(wallet: Wallet): Future[Int]
  def update(wallet: Wallet): Future[Int]
  def delete(id: Int): Future[Int]

  def getWalletByDate(date: Int): Future[List[Wallet]]
  def getChartBalance(account: String, date: Int): Future[List[Balance]]
  def getExpenses(year: Int): Future[List[Expenses]]
  def getMonthlyBalance(account: String, date: Option[Int] = None): Future[Int]
}

class WalletRepoImpl(client: DatabaseClient) extends WalletRepo {

  private val balanceParser: RowParser[Balance] = Macro.namedParser[Balance]
  private val expenseParser: RowParser[Expenses] = Macro.namedParser[Expenses]
  private val walletParser: RowParser[Wallet] = Macro.namedParser[Wallet]

  def getWalletByDate(date: Int): Future[List[Wallet]] = {
    client.withConnection { implicit connection =>
      SQL("""
           SELECT id, date, name, category, currency, amount, done, account
           FROM wallets WHERE date={date}
         """)
        .on("date" -> date)
        .as(walletParser.*)
    }
  }
  override def getChartBalance(account: String, date: Int): Future[List[Balance]] = {
    client.withConnection { implicit connection =>
      SQL("""
        SELECT date, SUM(monthly_expenses::int) OVER (PARTITION BY account ORDER BY date) AS sum FROM (
          SELECT date, account, SUM(amount) as monthly_expenses FROM wallets
          WHERE date <= {date} AND account = {account}
          GROUP BY date, account
        ) as w1 ORDER BY date desc LIMIT 12
      """)
        .on("account" -> account, "date" -> date)
        .as(balanceParser.*)
    }
  }

  override def getMonthlyBalance(account: String, date: Option[Int]): Future[Int] = {
    client.withConnection { implicit connection =>
      val done = if (date.isEmpty) Some(true) else None
      SQL("""
        SELECT SUM(amount) as sa FROM wallets
        WHERE account = {account}
        AND (done = {done} OR {done} IS NULL)
        AND (date <= {date} OR {date} IS NULL)
      """)
        .on("account" -> account, "done" -> done, "date" -> date)
        .as(SqlParser.int("sa").single)
    }
  }

  override def getExpenses(year: Int): Future[List[Expenses]] = {
    client.withConnection { implicit connection =>
      SQL("""
        SELECT category, sum(-amount) AS amount FROM wallets
        WHERE (date / 100) = {year}
          AND done = true
          AND account = 'DBS'
          AND category NOT IN ('Bonus', 'ROI', 'Salary', 'Temp', 'Transfer')
        GROUP BY category
      """)
        .on("year" -> year)
        .as(expenseParser.*)
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