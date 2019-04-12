package com.seanmcapp.view

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import com.seanmcapp.service.DotaService

import scala.concurrent.Future
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global

trait DotaView extends DotaService {

  def home: Future[HttpResponse] = {
    val template = templateSource("dota/home.html")
    val data = getRecentMatches

    val render = HttpEntity(template)
    Future(HttpResponse(entity = render))
  }

  def player: Future[HttpResponse] = {
    val template = templateSource("dota/player.html")
    val data = getPlayers

    val render = HttpEntity(template)
    Future(HttpResponse(entity = render))
  }

  def player(id: Int): Future[HttpResponse] = {
    val template = templateSource("dota/player.html")
    val data = getPlayerMatches(id)

    val render = HttpEntity(template)
    Future(HttpResponse(entity = render))
  }

  def hero: Future[HttpResponse] = {
    val template = templateSource("dota/hero.html")
    val data = getHeroes

    val render = HttpEntity(template)
    Future(HttpResponse(entity = render))
  }

  def hero(id: Int): Future[HttpResponse] = {
    val template = templateSource("dota/hero.html")
    val data = getHeroMatches(id)

    val render = HttpEntity(template)
    Future(HttpResponse(entity = render))
  }

  private def templateSource(source: String): String = Source.fromResource(source).mkString

}
