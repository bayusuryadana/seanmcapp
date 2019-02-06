package com.seanmcapp.api

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import com.seanmcapp.repository._
import com.seanmcapp.repository.instagram.{Customer, Photo, Vote}
import com.seanmcapp.util.parser.{BroadcastMessage, Result}
import com.seanmcapp.util.requestbuilder.TelegramRequest
import spray.json._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

trait WebAPI extends Service {

  import com.seanmcapp.util.parser.WebAPIJson._

  def get(request: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "latest" => photoRepo.getLatest.map(_.toJson)
      case "random" => photoRepo.getRandom(None).map(_.toJson)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  def post(request: JsValue, input: JsValue): Future[JsValue] = {
    request.asInstanceOf[JsString].value match {
      case "random" => randomFlow(input)
      case "vote" => voteFlow(input)
      case _ => Future.successful(JsString("no such method"))
    }
  }

  def stats(): Future[HttpResponse] = {
    val template = Source.fromResource("stats.html").mkString

    val photoRepoF = photoRepo.getAll
    val voteRepoF = voteRepo.getAll
    val customerRepoF = customerRepo.getAll
    val trackRepoF = trackRepo.getAll
    for {
      photos <- photoRepoF
      votes <- voteRepoF
      customers <- customerRepoF
      tracks <- trackRepoF
    } yield {
      val tr = "<tr>"
      val tre = "</tr>"
      val td = "<td>"
      val tde = "</td>"

      val photoMap = votes.groupBy(v => v.photoId).map(p => (p._1, p._2.map(_.rating)))
      val accountMap = photos.groupBy(p => p.account).map(a => (a._1, a._2.flatMap(p => photoMap.getOrElse(p.id, Seq.empty[Long]))))
      val accountVotesRank = accountMap.map { a =>
        val name = a._1
        val data = a._2
        val count = data.length
        val avg = (math rint (data.sum.toDouble/count) * 100) / 100
        Result(name, count, avg)
      }(collection.breakOut)
        .sortBy(-_.avg)
        .foldLeft("")((res, i) => res + tr + td + i.name + tde + td + i.count + tde + td + i.avg + tde + tre)

      val voteGroupByCustomer = votes.groupBy(v => v.customerId).map(c => (c._1,c._2.map(_.rating)))
      val customerVotesRank = customers.map { c =>
        val data = voteGroupByCustomer.getOrElse(c.id, Seq.empty[Long])
        val count = data.length
        val avg = (math rint (data.sum.toDouble/count) * 100) / 100
        Result(c.name, count, avg)
      }
        .filter(_.count > 0)
        .sortBy(-_.count)
        .foldLeft("")((res, i) => res + tr + td + i.name + tde + td + i.count + tde + td + i.avg + tde + tre)

      val trackGroupByCustomer = tracks.groupBy(t => t.customerId).map(c => (c._1,c._2.length))
      val customerTracksRank = customers.map(c => (c.name, trackGroupByCustomer.getOrElse(c.id, 0)))
        .filter(_._2 > 0)
        .sortBy(-_._2)
        .foldLeft("")((res, i) => res + tr + td + i._1 + tde + td + i._2 + tde + tre)

      val votesGroupByPhoto = votes.groupBy(v => v.photoId).map(c => (c._1,c._2.map(_.rating)))
      val photoRank = photos.map { c =>
        val data = votesGroupByPhoto.getOrElse(c.id, Seq.empty[Long])
        val count = data.length
        val avg = (math rint (data.sum.toDouble/count) * 100) / 100
        Result(c.caption, count, avg, c.account)
      }
        .filter(_.count > 0)
        .sortWith { (a,b) =>
        if (a.count == b.count) {
          a.avg > b.avg
        } else {
          a.count > b.count
        }
      }
        .foldLeft("")((res, i) => res + tr + td + i.name + tde + td + i.account + tde + td + i.count + tde + td + i.avg + tde + tre)

      val entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, template
        .replace("${accountVotesData}", accountVotesRank)
        .replace("${customerVotesData}", customerVotesRank)
        .replace("${customerTracksData}", customerTracksRank)
        .replace("${photoRankData}", photoRank)
      )
      HttpResponse(entity = entity)
    }
  }

  private def randomFlow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (RANDOM) =====\n" + input + "\n")
    val request = input.convertTo[Customer]
    val customer = Customer(request.id, request.name, request.platform)
    getRandom(customer, None, None)((p:Photo) => p).map(_.toJson)
  }

  private def voteFlow(input: JsValue): Future[JsValue] = {
    println("===== INPUT (VOTE) =====\n" + input + "\n")
    val request = input.convertTo[Vote]
    val vote = Vote(request.photoId, request.customerId, request.rating)
    doVote(vote).map(_ => 200.toJson)
  }

}
