package com.seanmcapp.service

import java.net.URLEncoder

import com.seanmcapp.external.{NCovClient, TelegramClient}

class NCovService(ncovClient: NCovClient, telegramClient: TelegramClient) extends ScheduledTask {

  override def run: String = {
    val resp = ncovClient.getReport
    val sgResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Singapore") => l.split(",").last }.head
    }

    val idResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Indonesia") => l.split(",").last }.head
    }

    val sgActiveCase = sgResults(0).toInt-sgResults(1).toInt-sgResults(2).toInt
    val idActiveCase = idResults(0).toInt-idResults(1).toInt-idResults(2).toInt
    val sgResult = s"Singapore new case: ${sgResults(0)}, active case: $sgActiveCase"
    val idResult = s"Indonesia new case: ${idResults(0)}, active case: $idActiveCase"
    telegramClient.sendMessage(-1001359004262L, URLEncoder.encode(s"$sgResult\n$idResult", "UTF-8"))
    s"$sgResult\n$idResult"
  }

}
