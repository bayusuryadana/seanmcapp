package com.seanmcapp.repository

import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait DBComponent {

  val config = DatabaseConfig.forConfig[JdbcProfile]("database")

  val db = config.db

  def run[T](query: DBIO[T]): Future[T] = db.run(query)

}