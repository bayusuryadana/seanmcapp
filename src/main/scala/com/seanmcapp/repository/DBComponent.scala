package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

trait DBComponent {

  val db: Database = Database.forConfig("db")

}