package com.seanmcapp.service

import java.text.NumberFormat

import com.seanmcapp.AmarthaConf
import com.seanmcapp.external.{AmarthaClient, AmarthaMitra, AmarthaTransaction, TelegramClient}
import com.seanmcapp.util.MonthUtil
import org.joda.time.DateTime

import scala.collection.parallel.CollectionConverters._

class AmarthaService(amarthaClient: AmarthaClient, telegramClient: TelegramClient) extends ScheduledTask {

  // $COVERAGE-OFF$
  def processResult(username: String, password: String): List[AmarthaMitra] = {
    val accessToken = getAccessToken(username, password)

    val amarthaMitraList = amarthaClient.getMitraList(accessToken)
    val mitraList = amarthaMitraList.portofolio.par.map { amarthaPortofolio =>
      val amarthaDetail = amarthaClient.getMitraDetail(accessToken, amarthaPortofolio.loanId)
      AmarthaMitra(amarthaPortofolio, amarthaDetail)
    }.toList

    mitraList
  }

  def getCSV(username: String, password: String): String = {
    val mitraList = processResult(username, password)
    val doubleMap = mitraList.map { mitra =>
      val installment = mitra.installment.map { installment =>
        installment.createdAt -> installment.frequency
      }.toMap
      mitra -> installment
    }.sortBy(_._1.detail.disbursementDate)

    val sortedDateSet = doubleMap.map(_._2).flatMap(_.keys).distinct.sorted
    val result = doubleMap.map { case (mitra, installment) =>
      val data = sortedDateSet.map { dateCol =>
        installment.get(dateCol).map(_.toString).getOrElse("")
      }
      s"${mitra.name}-${mitra.id}" -> data
    }

    val header = sortedDateSet.foldLeft(", ")((r, date) => r + s"$date, ")
    result.foldLeft(s"$header\n")((res, data) => res + s"${data._1}, ${data._2.foldLeft("")((r, num) => r + s"$num, ")}\n")
  }

  private def getAccessToken(username: String, password: String): String = {
    val authData = amarthaClient.getTokenAuth(username, password)
    authData.accessToken
  }
  // $COVERAGE-ON$

  override def run: String = {
    val amarthaConf = AmarthaConf()
    val accessToken = getAccessToken(amarthaConf.username, amarthaConf.password)
    val transactionList = amarthaClient.getTransaction(accessToken)
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
    telegramClient.sendMessage(274852283L, message)
    message
  }

}
