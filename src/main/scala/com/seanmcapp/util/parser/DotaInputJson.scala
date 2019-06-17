package com.seanmcapp.util.parser

import spray.json._

case class PlayerResponse(profile: ProfileResponse, rankTier: Option[Int])

case class ProfileResponse(personaName: String, avatarfull: String)

object DotaInputJson extends DefaultJsonProtocol {

  implicit val profileResponseFormat = jsonFormat(ProfileResponse, "personaname", "avatarfull")

  implicit val playerResponseFormat = jsonFormat(PlayerResponse, "profile", "rank_tier")

}