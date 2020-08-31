package com.seanmcapp.service

import java.net.URLEncoder

import com.seanmcapp.external.{AirVisualClient, TelegramClient}

class AirVisualService(airVisualClient: AirVisualClient, telegramClient: TelegramClient) extends ScheduledTask {

  private val AirGood = Array(0x1F340)
  private val AirModerate = Array(0x1F60E)
  private val AirSensitive = Array(0x1F630)
  private val AirUnhealthy = Array(0x1F637)
  // private val AirRisky = Array(0x1F480) -- Anti Rizky .|.

  override def run: String = {
    val cityResults = airVisualClient.getCityResults

    val stringMessage = cityResults.foldLeft("*Seanmcearth* melaporkan kondisi udara saat ini:") { (res, row) =>
      val city = row._1
      val aqius = row._2
      val appendString = "\n" + city.city + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
      res + appendString
    }
    telegramClient.sendMessage(-1001359004262L, URLEncoder.encode(stringMessage, "UTF-8"))
    stringMessage
  }

  private def getEmojiFromAqi(aqi: Int): String = {
    aqi match {
      case _ if aqi <= 50 => new String(AirGood, 0, AirGood.length)
      case _ if aqi > 50 & aqi <= 100 => new String(AirModerate, 0, AirModerate.length)
      case _ if aqi > 100 & aqi <= 150 => new String(AirSensitive, 0, AirSensitive.length)
      case _ if aqi > 150 => new String(AirUnhealthy, 0, AirUnhealthy.length)
    }
  }

}
