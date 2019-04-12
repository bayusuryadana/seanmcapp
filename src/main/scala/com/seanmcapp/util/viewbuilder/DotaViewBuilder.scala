package com.seanmcapp.util.viewbuilder

import com.seanmcapp.model.{HeroViewModel, MatchViewModel, PeerViewModel, PlayerViewModel}

import scala.io.Source

trait DotaViewBuilder {
  //TODO: this Trait is not yet implemented
  private val tr = "<tr>"
  private val tre = "</tr>"
  private val td = "<td>"
  private val tde = "</td>"

  def build1(items: Seq[MatchViewModel]): String = {
    templateSource("dota/home.html")
  }

  def build2(items: Seq[PlayerViewModel]): String = {
    templateSource("dota/player.html")
  }

  def build3(items: (Seq[MatchViewModel], Seq[PeerViewModel])): String = {
    templateSource("dota/player.html")
  }

  def build4(items: Seq[HeroViewModel]): String = {
    templateSource("dota/hero.html")
  }

  def build5(items: Seq[(String, Seq[MatchViewModel])]): String = {
    templateSource("dota/hero.html")
  }

  private def templateSource(source: String): String = Source.fromResource(source).mkString
}
