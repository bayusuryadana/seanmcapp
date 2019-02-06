package com.seanmcapp.repository.instagram

import com.seanmcapp.repository.DBComponent
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class AccountInfo(tag: Tag) extends Table[Account](tag, "accounts") {
  val id = column[Long]("id", O.PrimaryKey)
  val name = column[String]("name")
  val regex = column[String]("regex")

  def * = (id, name, regex) <> (Account.tupled, Account.unapply)
}

object AccountRepoImpl extends TableQuery(new AccountInfo(_)) with AccountRepo with DBComponent {

  def getAll: Future[Seq[Account]] = {
    run(this.result)
  }

}
