package com.seanmcapp.repository.postgre

import com.seanmcapp.repository.{Account, AccountRepo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class AccountInfo (tag: Tag) extends Table[Account](tag, "accounts") {
  val id = column[String]("id", O.PrimaryKey)
  val name = column[String]("name")
  val regex = column[String]("regex")

  def * = (id, name, regex) <> (Account.tupled, Account.unapply)
}

class AccountRepoImpl extends TableQuery(new AccountInfo(_)) with AccountRepo with DBComponent {

  def getAll: Future[Seq[Account]] = {
    run(this.result)
  }

}
