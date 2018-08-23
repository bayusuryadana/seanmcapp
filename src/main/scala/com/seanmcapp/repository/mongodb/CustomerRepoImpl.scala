package com.seanmcapp.repository.mongodb

import org.mongodb.scala.model.Filters._
import com.seanmcapp.repository.{Customer, CustomerRepo}
import org.mongodb.scala.model.ReplaceOptions

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CustomerRepoImpl extends DBComponent[Customer]("customers") with CustomerRepo {

  override def getAll: Future[Seq[Customer]] = collection.find().toFuture()

  override def update(customer: Customer): Future[Option[Customer]] = {
    collection.replaceOne(equal("_id", customer.id), customer, ReplaceOptions().upsert(true)).toFutureOption()
      .map(res => if (res.exists(_.getModifiedCount == 1)) Some(customer) else None)
  }

}
