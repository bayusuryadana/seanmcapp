package com.seanmcapp.repository.seanmcwallet

import java.util.concurrent.TimeUnit

import com.seanmcapp.repository.DBComponent
import com.seanmcapp.service.WalletUtils._
import com.seanmcapp.util.MemoryCache
import io.circe.syntax._
import io.circe.Printer
import io.circe.generic.auto._
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import scalacache.Cache
import scalacache.modes.sync._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, FiniteDuration}

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

object WalletRepoDemo extends WalletRepo with MemoryCache {

  implicit val walletCache: Cache[Seq[Wallet]] = createCache[Seq[Wallet]]
  implicit val countCache: Cache[Int] = createCache[Int]
  val duration: FiniteDuration = Duration(1, TimeUnit.HOURS)
  private val key = "wallet-demo"
  private val count = "wallet-count"
  
  override def getAll: Future[Seq[Wallet]] = {
    val res = walletCache.get(key).getOrElse(getMockData)
    walletCache.put(key)(res, Some(duration))
    Future.successful(res)
  }

  override def insert(wallet: Wallet): Future[Int] = {
    val currentData = walletCache.get(key).get
    val id = countCache.get(count).getOrElse(-1)
    val data = wallet.copy(id = id)
    val addedData = currentData :+ data
    walletCache.put(key)(addedData, Some(duration))
    countCache.put(count)(id-1, Some(duration))
    Future.successful(1)
  }

  override def update(wallet: Wallet): Future[Int] = {
    val currentData = walletCache.get(key).get
    val index = currentData.indexWhere(_.id == wallet.id)
    val updatedData = currentData.patch(index, Seq(wallet), 1)
    walletCache.put(key)(updatedData, Some(duration))
    Future.successful(1)
  }

  override def delete(id: Int): Future[Int] = {
    val currentData = walletCache.get(key).get
    val deletedData = currentData.filterNot(_.id == id)
    walletCache.put(key)(deletedData, Some(duration))
    Future.successful(1)
  }
  
  private def getMockData: Seq[Wallet] = {
    val todayMonth = new DateTime().toDateTime(DateTimeZone.forID("+07:00"))
    val formatter = DateTimeFormat.forPattern("yyyyMM")
    val date = formatter.print(todayMonth).toInt
    
    val data = Seq(0,1,2,3,4,5)
    val r = 8
    data.flatMap { i =>
      val iDate = (date-i).adjustDate
      Seq(
        Wallet(r*i+1, iDate, "Salary", "Salary", "SGD", 4000, true, "DBS"),
        Wallet(r*i+2, iDate, "Room rent", "Rent", "SGD", -1000, true, "DBS"),
        Wallet(r*i+3, iDate, "Daily", "Daily", "SGD", -800, true, "DBS"),
        Wallet(r*i+4, iDate, "from SGD", "Transfer", "SGD", -1000, true, "DBS"),
        Wallet(r*i+5, iDate, "to IDR", "Transfer", "IDR", 1100000, true, "BCA"),
        Wallet(r*i+6, iDate, "Stock", "Funding", "IDR", -5000000, true, "BCA"),
        Wallet(r*i+7, iDate, "Gym", "Wellness", "SGD", -500, true, "DBS"),
        Wallet(r*i+8, iDate, "Games", "IT Stuff", "SGD", -500, true, "DBS")
      )
    }
  }
}
