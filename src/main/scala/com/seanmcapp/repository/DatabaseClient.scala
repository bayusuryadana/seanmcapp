package com.seanmcapp.repository

import com.seanmcapp.util.DatabaseConf
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

import java.sql.Connection
import scala.concurrent.{ExecutionContext, Future}

class DatabaseClient(implicit ec: ExecutionContext) {

  private val config = new HikariConfig()
  private val dbConf = DatabaseConf()

  config.setJdbcUrl(s"jdbc:postgresql://${dbConf.host}:5432/${dbConf.name}")
  config.setUsername(dbConf.user)
  config.setPassword(dbConf.pass)
  config.setMaximumPoolSize(10) // Adjust pool size as needed

  private val dataSource = new HikariDataSource(config)

  // Provide connection when needed
  def withConnection[T](block: Connection => T): Future[T] = {
    Future {
      val conn = dataSource.getConnection
      try block(conn)
      finally conn.close()
    }
  }

  // Shutdown pool on app exit
  def close(): Unit = dataSource.close()
}
