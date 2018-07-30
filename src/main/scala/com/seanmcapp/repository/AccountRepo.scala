package com.seanmcapp.repository

import scala.concurrent.Future

case class Account(id: String, name: String, regex: String)

trait AccountRepo {

  def getAll: Future[Seq[Account]]

}
