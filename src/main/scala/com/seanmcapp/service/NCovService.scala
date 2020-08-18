package com.seanmcapp.service

import java.net.URLEncoder

import com.seanmcapp.external.{NCovClient, TelegramClient}

class NCovService(ncovClient: NCovClient, telegramClient: TelegramClient) extends ScheduledTask {

  override def run: Any = {

    val resp = ncovClient.getReport
    val sgResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Singapore") => l.split(",").last }.head
    }

    val idResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Indonesia") => l.split(",").last }.head
    }

    val result = s"Singapore case Confirmed: ${sgResults(0)}, Death: ${sgResults(1)}, Recovered: ${sgResults(2)}\nIndonesia case Confirmed: ${idResults(0)}, Death: ${idResults(1)}, Recovered: ${idResults(2)}"
    telegramClient.sendMessage(-1001359004262L, URLEncoder.encode(result, "UTF-8"))
    result
  }

}
