package com.seanmcapp.repository.mongodb

import com.seanmcapp.repository.{Account, AccountRepo}

import scala.concurrent.Future

class AccountRepoImpl extends DBComponent[Account]("accounts") with AccountRepo {

  override def getAll: Future[Seq[Account]] = collection.find().toFuture()

}
