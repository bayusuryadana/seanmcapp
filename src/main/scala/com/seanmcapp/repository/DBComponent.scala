package com.seanmcapp.repository

import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait DBComponent {

  val db = DatabaseConfig.forConfig[JdbcProfile]("database").db

  def run[T](query: DBIO[T]): Future[T] = try db.run(query)

}