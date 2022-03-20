package com.seanmcapp.service

import com.seanmcapp.external.TelegramClient
import com.seanmcapp.util.{DayUtil, MonthUtil}
import org.joda.time.{DateTime, DateTimeZone}

import scala.io.Source

class SportsService(telegramClient: TelegramClient) extends ScheduledTask {

  private val SGT = "+08:00"
  
  override def run: String = {
    val currentTime = new DateTime().toDateTime(DateTimeZone.forID(SGT))
    
    val kontol = Source.fromResource("news/sports.csv").getLines().toList.tail
    kontol.map { line =>
      SportsSchedule(line.mkString.split(","))
    }.filter { sched =>
      currentTime.getMillis < sched.time.getMillis && currentTime.getMillis > sched.time.minusHours(24).getMillis
    }.foreach { upcomingEvent =>
      val event = upcomingEvent.`type` match {
        case "f1" => "F1"
        case "motogp" => "motoGP"
        case _ => "Unknown"
      }
      
      val monthMap = MonthUtil.map.map{ case (k, v) => v.toInt -> k}
      val date = s"${DayUtil.map(upcomingEvent.time.getDayOfWeek)}, ${upcomingEvent.time.getDayOfMonth} ${monthMap(upcomingEvent.time.getMonthOfYear)}"
      val hour = if (upcomingEvent.time.getHourOfDay < 10) s"0${upcomingEvent.time.getHourOfDay}" else upcomingEvent.time.getHourOfDay.toString 
      val minute = if (upcomingEvent.time.getMinuteOfHour < 10) s"0${upcomingEvent.time.getMinuteOfHour}" else upcomingEvent.time.getMinuteOfHour.toString
      val time = s"$hour:$minute"
      val message = s"[$event] #${upcomingEvent.number}\n${upcomingEvent.country.replaceAll("^\"|\"$", "")} - ${upcomingEvent.name.replaceAll("^\"|\"$", "")}\n$date. $time"
      println(message)
      telegramClient.sendMessage(274852283, message)
    }
    ""
  }
  
}

case class SportsSchedule(time: DateTime, country: String, name: String, number: Int, `type`: String)
object SportsSchedule {
  
  private val year = 2022
  private val SGT = "+08:00"
  
  def apply(items: Array[String]): SportsSchedule = {
    val epochTime = new DateTime()
      .withZone(DateTimeZone.forID(SGT))
      .withYear(year)
      .withMonthOfYear(items(1).toInt)
      .withDayOfMonth(items(0).toInt)
      .withHourOfDay(items(2).toInt / 100)
      .withSecondOfMinute(0)
      .withMillisOfSecond(0)
      .withMinuteOfHour(items(2).toInt % 100)
    SportsSchedule(epochTime, items(3), items(4), items(5).toInt, items(6))
  }
}
