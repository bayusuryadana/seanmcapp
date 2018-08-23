package com.seanmcapp.repository.mongodb

import org.mongodb.scala.model.Filters._
import com.seanmcapp.repository.{Vote, VoteRepo}
import org.mongodb.scala.model.ReplaceOptions

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class VoteRepoImpl extends DBComponent[Vote]("votes") with VoteRepo {

  override def update(vote: Vote): Future[Option[Vote]] = {
    collection.replaceOne(and(equal("photos_id", vote.photoId), equal("customers_id", vote.customerId)), vote, ReplaceOptions().upsert(true))
      .toFutureOption()
      .map(res => if (res.exists(_.getModifiedCount == 1)) Some(vote) else None)
  }

}
