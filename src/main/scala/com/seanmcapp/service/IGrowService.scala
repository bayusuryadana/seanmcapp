package com.seanmcapp.service

import com.seanmcapp.config.SchedulerConf
import com.seanmcapp.external.{IGrowClient, TelegramClient}

class IGrowService(igrowClient: IGrowClient, telegramClient: TelegramClient) extends ScheduledTask {

  override def run: Any = {
    val igrowResponse = igrowClient.getList
    val schedulerConf = SchedulerConf()
    igrowResponse.data.filter(_.stock > 0).foreach { data =>
      val stringMessage = s"${data.name}%0A${data.price}%0Asisa ${data.stock} unit"
      schedulerConf.igrow.foreach(chatId => telegramClient.sendMessage(chatId, stringMessage))
    }
  }

}
