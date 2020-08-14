package com.seanmcapp.scheduler

import java.text.NumberFormat

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.AmarthaConf
import com.seanmcapp.external.TelegramClient
import com.seanmcapp.service.AmarthaService
import com.seanmcapp.util.MonthUtil
import com.seanmcapp.util.parser.AmarthaTransaction
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

class AmarthaScheduler (startTime: Int, interval: FiniteDuration, amarthaService: AmarthaService,
                        override val http: HttpRequestBuilder)
                       (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with TelegramClient {

  override def task: String = {
    val amarthaConf = AmarthaConf()
    val transactionList = amarthaService.processResult(amarthaConf.username, amarthaConf.password).transaction
    val currentDateString = DateTime.now().minusDays(1).toString("YYYYMMdd")

    val transactionMap = transactionList.map { t =>
      val dateString = t.date.split(" ")
      val date = dateString(0)
      val month = MonthUtil.map.getOrElse(dateString(1), new Exception(s"cannot find month mapping for: ${dateString(1)}"))
      val year = dateString(2)
      t.copy(date = s"${year+month+date}")
    }.groupBy(_.date)
    val transactionToday = transactionMap.getOrElse(currentDateString, List.empty[AmarthaTransaction]) // TODO: need better handle
    val revenueToday = transactionToday.map(_.debit.replaceAll("\\.","").toLong).sum
    val revenueTodayStringFormat = NumberFormat.getIntegerInstance.format(revenueToday)
    val message = s"[Amartha]%0AToday's revenue: Rp. $revenueTodayStringFormat"
    sendMessage(274852283L, message)
    message
  }
}
