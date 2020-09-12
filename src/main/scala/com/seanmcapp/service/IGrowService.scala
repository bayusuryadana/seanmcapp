package com.seanmcapp.service

import com.seanmcapp.SchedulerConf
import com.seanmcapp.external.{IGrowClient, TelegramClient}

class IGrowService(igrowClient: IGrowClient, telegramClient: TelegramClient) extends ScheduledTask {

  private[service] val schedulerConf = SchedulerConf()

  override def run: Seq[String] = {
    val igrowResponse = igrowClient.getList
    igrowResponse.data.filter(_.stock > 0).flatMap { data =>
      val stringMessage = s"${data.name}%0APrice: ${data.price}%0AContract: ${data.expired_label}%0AReturn: ${data.`return`}%0A${data.stock} unit left"
      schedulerConf.igrow.map { chatId =>
        telegramClient.sendMessage(chatId, stringMessage)
        stringMessage
      }
    }
  }

}
