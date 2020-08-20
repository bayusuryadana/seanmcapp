package com.seanmcapp.service

import com.seanmcapp.external.{DsdaJakartaClient, TelegramClient}

class DsdaJakartaService(dsdaJakartaClient: DsdaJakartaClient, telegramClient: TelegramClient) extends ScheduledTask {

  private val NORMAL_STATUS = "Normal"

  override def run: String = {
    val dsdaJakartaResponse = dsdaJakartaClient.getReport
    val waterGates = dsdaJakartaResponse.waterGates
      .filter(w => !w.status.split(":")(1).trim.equalsIgnoreCase(NORMAL_STATUS))

    if (waterGates.nonEmpty) {
      val result = new java.lang.StringBuilder
      result.append(s"Seanmcapp melaporkan pintu air siaga:\n")

      waterGates.map(w => result.append(s"\n${w.name.trim}: ${w.status.split(":")(1).trim}"))

      telegramClient.sendMessage(-1001359004262L, result.toString)
      result.toString
    } else {
      ""
    }
  }

}
