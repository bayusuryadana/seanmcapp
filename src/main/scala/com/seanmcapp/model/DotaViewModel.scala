package com.seanmcapp.model

import org.joda.time.DateTime

case class MatchViewModel(name: String, matchId: Long, side: String, result: String, duration: String, mode: String,
                          hero: String, startTime: DateTime, kda: String)

case class PlayerViewModel(name: String, personaName: String, mmrEstimate: Int)

case class PeerViewModel(peerName: String, win: Int, percentage:Double)

case class HeroViewModel(id: Int, localizedName: String, primaryAttr: String, attackType: String)