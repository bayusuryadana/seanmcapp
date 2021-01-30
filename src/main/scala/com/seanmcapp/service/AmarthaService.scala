package com.seanmcapp.service

import java.text.NumberFormat

import com.seanmcapp.AmarthaConf
import com.seanmcapp.external._
import com.seanmcapp.util.MonthUtil
import org.joda.time.DateTime

import scala.collection.SortedMap
import scala.collection.parallel.CollectionConverters._

class AmarthaService(amarthaClient: AmarthaClient, telegramClient: TelegramClient) extends ScheduledTask {

  private val amarthaConf = AmarthaConf()

  private def getMitraList(accessToken: String): List[AmarthaMitra] = {
    val amarthaMitraList = amarthaClient.getMitraList(accessToken)
    val mitraList = amarthaMitraList.portofolio.par.map { amarthaPortofolio =>
      val amarthaDetail = amarthaClient.getMitraDetail(accessToken, amarthaPortofolio.loanId)
      AmarthaMitra(amarthaPortofolio, amarthaDetail)
    }.toList

    mitraList
  }

  def getAmarthaView(): AmarthaView = {
    val accessToken = getAccessToken(amarthaConf.username, amarthaConf.password)
    val summary = amarthaClient.getAllSummary(accessToken)
    val mitraList = getMitraList(accessToken)
    val doubleMap = mitraList.map { mitra =>
      val installment = mitra.installment.groupBy(_.createdAt).toSeq.map { case (date, installment) =>
        date -> installment.map(_.frequency).sum
      }.toMap
      mitra -> installment
    }.sortBy(_._1.detail.disbursementDate)

    val sortedDateSet = doubleMap.map(_._2).flatMap(_.keys).distinct.sorted
    val result = doubleMap.map { case (mitra, installment) =>
      val data = sortedDateSet.map { dateCol =>
        installment.get(dateCol).map(_.toString).getOrElse("")
      }
      val numberOfRemainingPayment = 50 - data.flatMap(_.toIntOption).sum
      val remainingPaymentAmount = (numberOfRemainingPayment * mitra.detail.weeklyPayment).formatNumber

      mitra.id -> AmarthaMitraView(mitra.id, mitra.name, s"${mitra.detail.ROIPercentage}%", numberOfRemainingPayment, remainingPaymentAmount, data)
    }.to(SortedMap)

    val totalAmountLeft = result.values.map(_.remainingPaymentAmount.replace(",","").toLong).sum.formatNumber
    AmarthaView(summary.nilaiAset.formatNumber, summary.totalAllrevenue.formatNumber, totalAmountLeft, sortedDateSet, result)
  }

  private def getAccessToken(username: String, password: String): String = {
    val authData = amarthaClient.getTokenAuth(username, password)
    authData.accessToken
  }

  override def run: String = {
    val accessToken = getAccessToken(amarthaConf.username, amarthaConf.password)
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

  implicit class Formatter(in: Long) {
    def formatNumber: String = {
      val formatter = NumberFormat.getIntegerInstance
      formatter.format(in)
    }
  }

}
