package com.seanmcapp.repository

import org.mongodb.scala.bson.annotations.BsonProperty

import scala.concurrent.Future

case class Customer(@BsonProperty("_id") id: Long, name: String, platform: String)

trait CustomerRepo {

  def getAll: Future[Seq[Customer]]

  def update(customer: Customer): Future[Option[Customer]]

}
