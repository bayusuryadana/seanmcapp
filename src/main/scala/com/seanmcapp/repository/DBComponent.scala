package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

object DBComponent {

  val db: Database = Database.forConfig("db")

}