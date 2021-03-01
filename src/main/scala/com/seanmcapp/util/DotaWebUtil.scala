package com.seanmcapp.util

import com.seanmcapp.external.MatchResponse
import com.seanmcapp.repository.dota.Hero
import com.seanmcapp.service.HomePageResponse

object DotaWebUtil {
  
  implicit class HeroObject(hero: Hero) {
    def getPrimaryColor: String = {
      hero.primaryAttr match {
        case "str" => "fg-red"
        case "agi" => "fg-green"
        case "int" => "fg-blue"
      }
    }

    def getRoles: String = hero.roles.split(",").map(_ + ", ").mkString.stripSuffix(", ")

    def getEscapedLore: Array[String] = {
      import scala.reflect.runtime.universe._
      hero.lore.split("\n\n").map { l =>
        Literal(Constant(l)).toString.stripSuffix("\"").stripPrefix("\"")
      }
    }
  }
  
  implicit class MatchResponseObject(mr: MatchResponse) {
    
    def getResult: String = if (mr.player_slot < 100 ^ mr.radiant_win) "Lost" else "Won"

    def getCssResult: String = if (mr.player_slot < 100 ^ mr.radiant_win) "fg-red" else "fg-green"

    def getDuration: String = s"${mr.duration / 60}:${mr.duration % 60}"

    def getKDA: String = s"${mr.kills}/${mr.deaths}/${mr.assists}"
    
  }
  
  implicit class HomePageResponseObject(homePage: HomePageResponse) {
    def heroImageMap: Map[Int, String] = homePage.heroes.map(h => h.hero.id -> h.hero.image).toMap
  }
  
}
