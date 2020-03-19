package com.seanmcapp.util.parser.decoder

case class PlayerResponse(profile: ProfileResponse, rankTier: Option[Int])

case class ProfileResponse(personaName: String, avatarfull: String)

trait DotaMetadataDecoder extends JsonDecoder {

  implicit val profileResponseFormat = jsonFormat(ProfileResponse, "personaname", "avatarfull")

  implicit val playerResponseFormat = jsonFormat(PlayerResponse, "profile", "rank_tier")

}