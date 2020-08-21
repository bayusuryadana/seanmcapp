package com.seanmcapp.service

import java.text.NumberFormat

import com.seanmcapp.config.AmarthaConf
import com.seanmcapp.external.{AmarthaClient, AmarthaResult, AmarthaTransaction, TelegramClient}
import com.seanmcapp.util.MonthUtil
import org.joda.time.DateTime

import scala.util.Try

class AmarthaService(amarthaClient: AmarthaClient, telegramClient: TelegramClient) extends ScheduledTask {

  // $COVERAGE-OFF$
  def processResult(username: String, password: String): AmarthaResult = {
    val authData = amarthaClient.getTokenAuth(username, password)
    val accessToken = authData.accessToken

    val summary = amarthaClient.getAllSummary(accessToken).copy(namaInvestor = Some(authData.name))

    val amarthaMitraList = amarthaClient.getMitraList(accessToken)
    val mitraList = amarthaMitraList.portofolio.map { amarthaPortofolio =>
      val amarthaDetail = amarthaClient.getMitraDetail(accessToken, amarthaPortofolio.loanId)
      amarthaPortofolio.copy(
        area = Some(amarthaDetail.loan.areaName),
        branchName = Some(amarthaDetail.loan.branchName),
        dueDate = Some(amarthaDetail.loan.dueDate),
        installment = Some(amarthaDetail.installment),
        provinceName = Some(amarthaDetail.loan.provinceName),
        scheduleDay = Some(amarthaDetail.loan.scheduleDay),
        sector = Some(amarthaDetail.loan.sector),
      )
    }
    val mitraIdNameMap = mitraList.map { amarthaPortfolio =>
      amarthaPortfolio.loanId -> amarthaPortfolio.name
    }.toMap
    val transactionList = amarthaClient.getTransaction(accessToken).map { amarthaTransaction =>
      val idOpt = Try(amarthaTransaction.loanId.toLong).toOption
      val borrowerNameOpt = idOpt.flatMap(id => mitraIdNameMap.get(id))
      amarthaTransaction.copy(borrowerName = borrowerNameOpt)
    }

    AmarthaResult(summary, mitraList, transactionList)
  }
  // $COVERAGE-ON$

  override def run: String = {
    val amarthaConf = AmarthaConf()
    val transactionList = processResult(amarthaConf.username, amarthaConf.password).transaction
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
