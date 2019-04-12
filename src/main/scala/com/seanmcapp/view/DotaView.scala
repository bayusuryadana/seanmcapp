package com.seanmcapp.view

import akka.http.scaladsl.model.HttpResponse
import com.seanmcapp.service.DotaService
import com.seanmcapp.util.viewbuilder.DotaViewBuilder

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait DotaView extends DotaService with DotaViewBuilder {

  def home: Future[HttpResponse] = getRecentMatches.map(d => HttpResponse(entity = build1(d)))

  def player: Future[HttpResponse] = getPlayers.map(d => HttpResponse(entity = build2(d)))

  def player(id: Int): Future[HttpResponse] = getPlayerMatches(id).map(d => HttpResponse(entity = build3(d)))

  def hero: Future[HttpResponse] = getHeroes.map(d => HttpResponse(entity = build4(d)))

  def hero(id: Int): Future[HttpResponse] = getHeroMatches(id).map(d => HttpResponse(entity = build5(d)))

}
