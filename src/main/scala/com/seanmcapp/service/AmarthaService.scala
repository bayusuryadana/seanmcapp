package com.seanmcapp.service

import java.text.NumberFormat

import com.seanmcapp.AmarthaConf
import com.seanmcapp.external._
import com.seanmcapp.util.MonthUtil
import org.joda.time.DateTime

class AmarthaService(amarthaClient: AmarthaClient, telegramClient: TelegramClient) extends ScheduledTask {

  private val amarthaConf = AmarthaConf()

  override def run: String = {
    val accessToken = amarthaClient.getTokenAuth(amarthaConf.username, amarthaConf.password).accessToken
    val transactionList = amarthaClient.getTransaction(accessToken)
    val currentDateString = DateTime.now().toString("YYYYMMdd")

    val transactionMap = transactionList.map { t =>
      val dateString = t.date.split(" ")
      val date = dateString(0)
      val month = MonthUtil.map.getOrElse(dateString(1), new Exception(s"cannot find month mapping for: ${dateString(1)}"))
      val year = dateString(2)
      t.copy(date = s"${year+month+date}")
    }.groupBy(_.date)
    val roiToday = transactionMap.getOrElse(currentDateString, List.empty[AmarthaTransaction])
      .filter(_.`type` == AmarthaTransactionType.ROI) // TODO: need better handle
    val revenueToday = roiToday.map(_.debit.replaceAll("\\.","").toLong).sum
    val revenueTodayStringFormat = NumberFormat.getIntegerInstance.format(revenueToday)
    val currentBalance = transactionList.head.saldo
    val paidPercentDecimal = roiToday.count(_.debit != "0").toDouble / roiToday.length
    val message =
      s"""[Amartha]
         |Today's revenue: Rp. $revenueTodayStringFormat
         |Current balance: Rp. $currentBalance
         |Paid percentage: ${(paidPercentDecimal * 100).toInt}%""".stripMargin
    telegramClient.sendMessage(274852283L, message)
    message
  }

}
