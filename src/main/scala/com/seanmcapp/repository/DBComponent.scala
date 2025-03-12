package com.seanmcapp.repository

import slick.basic.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait DBComponent {

  def run[T](query: DBIO[T]): Future[T] = DBComponent.db.run(query)

}

object DBComponent {

  private val config = DatabaseConfig.forConfig[JdbcProfile]("database")

  private val db = config.db

}
