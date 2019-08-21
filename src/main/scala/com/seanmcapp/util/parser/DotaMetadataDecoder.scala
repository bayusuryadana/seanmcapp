package com.seanmcapp.util.parser

case class PlayerResponse(profile: ProfileResponse, rankTier: Option[Int])

case class ProfileResponse(personaName: String, avatarfull: String)

trait DotaMetadataDecoder extends Decoder {

  implicit val profileResponseFormat = jsonFormat(ProfileResponse, "personaname", "avatarfull")

  implicit val playerResponseFormat = jsonFormat(PlayerResponse, "profile", "rank_tier")

}