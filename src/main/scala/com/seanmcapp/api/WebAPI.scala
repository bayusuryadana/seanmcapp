package com.seanmcapp.api

import com.seanmcapp.repository._
import com.seanmcapp.util.parser.{BroadcastMessage, Result}
import com.seanmcapp.util.requestbuilder.TelegramRequest
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait WebAPI extends Service {

  import com.seanmcapp.util.parser.WebAPIJson._

  def get(request: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "latest" => photoRepo.getLatest.map(_.toJson)
      case "random" => photoRepo.getRandom.map(_.toJson)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  def post(request: JsValue, input: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "random" => randomFlow(input)
      case "vote" => voteFlow(input)
      case "broadcast" => broadcastFlow(input)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  def stats(request: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "account_rank" => accountRank
      case "customer_votes_rank" => customerVotesRank
      case "customer_tracks_rank" => customerTracksRank
      case "photo_rank" => photoRank
      case _ => Future.successful(JsString("no such method"))
    }
  }

  private def randomFlow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (RANDOM) =====\n" + input + "\n")
    val request = input.convertTo[Customer]
    val customer = Customer(request.id, request.name, request.platform)
    getRandom[Photo](customer, None, (p:Photo) => p).map(_.toJson)
  }

  private def voteFlow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (VOTE) =====\n" + input + "\n")
    val request = input.convertTo[Vote]
    val vote = Vote(request.photoId, request.customerId, request.rating)
    doVote(vote).map(_ => 200.toJson)
  }

  private def broadcastFlow(input: JsValue): Future[JsValue] = {
    val telegramRequest = new TelegramRequest {}
    val request = input.convertTo[BroadcastMessage]
    if (telegramRequest.telegramConf.key == request.key) {
      if (request.recipient == 0) {
        val customerRepoFuture = customerRepo.getAll
        for {
          customerRepo <- customerRepoFuture
        } yield {
          val result = customerRepo.map { subscriber =>
            telegramRequest.getTelegramSendMessege(subscriber.id, request.message).isSuccess
          }.reduce { (a, b) => a && b }
          JsBoolean(result)
        }
      } else {
        // my telegram id = 274852283L
        Future.successful(JsBoolean(telegramRequest.getTelegramSendMessege(request.recipient, request.message).isSuccess))
      }
    } else {
      Future.successful(JsString("wrong key"))
    }
  }

  private def accountRank = {
    val photoRepoF = photoRepo.getAll
    val voteRepoF = voteRepo.getAll
    for {
      photos <- photoRepoF
      votes <- voteRepoF
    } yield {
      val photoMap = votes.groupBy(v => v.photoId).map(p => (p._1, p._2.map(_.rating)))
      val accountMap = photos.groupBy(p => p.account).map(a => (a._1, a._2.flatMap(p => photoMap.getOrElse(p.id, Seq.empty[Long]))))

      val result = accountMap.map { a =>
        val name = a._1
        val data = a._2
        val count = data.length
        val avg = "%.2f".format(data.sum.toDouble / count)
        Result(name, count, avg)
      }(collection.breakOut)

      result.sortBy(-_.count).toJson
    }
  }

  private def customerVotesRank = {
    val customerRepoF = customerRepo.getAll
    val voteRepoF = voteRepo.getAll
    for {
      customers <- customerRepoF
      votes <- voteRepoF
    } yield {
      val customerMap = votes.groupBy(v => v.customerId).map(c => (c._1,c._2.map(_.rating)))
      val result = customers.map { c =>
        val data = customerMap.getOrElse(c.id, Seq.empty[Long])
        val count = data.length
        val avg = "%.2f".format(data.sum.toDouble / count)
        Result(c.name, count, avg)
      }.filter(_.count > 0)

      result.sortBy(-_.count).toJson
    }
  }

  private def customerTracksRank = {
    val customerRepoF = customerRepo.getAll
    val trackRepoF = trackRepo.getAll
    for {
      customers <- customerRepoF
      tracks <- trackRepoF
    } yield {
      val customerMap = tracks.groupBy(t => t.customerId).map(c => (c._1,c._2.length))
      customers.map(c => (c.name, customerMap.getOrElse(c.id, 0))).toJson
    }
  }

  private def photoRank = {
    val photoRepoF = photoRepo.getAll
    val voteRepoF = voteRepo.getAll
    for {
      photos <- photoRepoF
      votes <- voteRepoF
    } yield {
      val photoMap = votes.groupBy(v => v.photoId).map(c => (c._1,c._2.map(_.rating)))
      val result = photos.map { c =>
        val data = photoMap.getOrElse(c.id, Seq.empty[Long])
        val count = data.length
        val avg = "%.2f".format(data.sum.toDouble / count)
        Result(c.caption, count, avg)
      }.sortWith { (a,b) =>
        if (a.count == b.count) {
          a.avg > b.avg
        } else {
          a.count > b.count
        }
      }

      result .toJson
    }
  }


}
