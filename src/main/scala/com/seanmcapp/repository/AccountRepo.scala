package com.seanmcapp.repository

import org.mongodb.scala.bson.annotations.BsonProperty

import scala.concurrent.Future

case class Account(@BsonProperty("_id") id: String, name: String, regex: String)

trait AccountRepo {

  def getAll: Future[Seq[Account]]

}
