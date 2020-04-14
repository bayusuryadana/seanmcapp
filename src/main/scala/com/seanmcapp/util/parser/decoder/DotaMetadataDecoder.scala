package com.seanmcapp.util.parser.decoder

import com.seanmcapp.repository.dota.HeroAttribute

case class PlayerResponse(profile: ProfileResponse, rankTier: Option[Int])

case class ProfileResponse(personaName: String, avatarfull: String)

case class HeroResponse(id: Int, localizedName: String, primaryAttr: String, attackType: String, roles: List[String],
                        img: String, icon: String)


trait DotaMetadataDecoder extends JsonDecoder {

  implicit val profileResponseFormat = jsonFormat(ProfileResponse, "personaname", "avatarfull")

  implicit val playerResponseFormat = jsonFormat(PlayerResponse, "profile", "rank_tier")

  implicit val heroResponseAttribute = jsonFormat(HeroResponse, "id", "localized_name", "primary_attr", "attack_type", "roles", "img", "icon")

  implicit val heroAttributeResponseFormat = jsonFormat(HeroAttribute, "id", "base_health", "base_health_regen", "base_mana", "base_mana_regen",
    "base_armor", "base_mr", "base_attack_min", "base_attack_max", "base_str", "base_agi", "base_int", "str_gain", "agi_gain", "int_gain", "attack_range",
    "projectile_speed", "attack_rate", "move_speed", "turn_rate", "cm_enabled")

}