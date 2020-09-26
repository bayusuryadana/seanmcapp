package com.seanmcapp.service

import java.net.URLEncoder

import com.seanmcapp.external.{NCovClient, TelegramClient}

class NCovService(ncovClient: NCovClient, telegramClient: TelegramClient) extends ScheduledTask {

  case class NCovResult(today: String, yesterday: String)

  override def run: String = {
    val resp = ncovClient.getReport
    val sgResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Singapore") =>
        val numbers = l.split(",")
        NCovResult(numbers.last, numbers.dropRight(1).last)
      }.head
    }

    val idResults = resp.map { u =>
      u.split("\n").collect { case l if l.contains("Indonesia") =>
        val numbers = l.split(",")
        NCovResult(numbers.last, numbers.dropRight(1).last)
      }.head
    }

    val sgActiveCase = sgResults(0).today.toInt-sgResults(1).today.toInt-sgResults(2).today.toInt
    val idActiveCase = idResults(0).today.toInt-idResults(1).today.toInt-idResults(2).today.toInt
    val sgTodayCase = sgResults(0).today.toInt-sgResults(0).yesterday.toInt
    val idTodayCase = idResults(0).today.toInt-idResults(0).yesterday.toInt
    val sgResult = s"Singapore total reported cases: ${sgResults(0).today}\ntoday's new cases: $sgTodayCase\ndeath cases: ${sgResults(1).today}\nrecovered cases: ${sgResults(2).today}\nactive case: $sgActiveCase\n"
    val idResult = s"Indonesia total reported cases: ${idResults(0).today}\ntoday's new cases: $idTodayCase\ndeath cases: ${idResults(1).today}\nrecovered cases: ${idResults(2).today}\nactive case: $idActiveCase\n"
    telegramClient.sendMessage(-1001359004262L, URLEncoder.encode(s"$sgResult\n$idResult", "UTF-8"))
    s"$sgResult\n$idResult"
  }

}
