package com.seanmcapp.service

import com.seanmcapp.SchedulerConf
import com.seanmcapp.external.{IGrowClient, TelegramClient}

class IGrowService(igrowClient: IGrowClient, telegramClient: TelegramClient) extends ScheduledTask {

  private[service] val schedulerConf = SchedulerConf()

  override def run: Seq[String] = {
    val igrowResponse = igrowClient.getList
    igrowResponse.data.filter(_.stock > 0).flatMap { data =>
      val stringMessage = s"${data.name}\nPrice: ${data.price}\nContract: ${data.expired_label}\nReturn: ${data.`return`}\n${data.stock} unit left"
      schedulerConf.igrow.map { chatId =>
        telegramClient.sendMessage(chatId, stringMessage)
        stringMessage
      }
    }
  }

}
