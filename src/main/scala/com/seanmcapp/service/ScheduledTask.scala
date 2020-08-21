package com.seanmcapp.service

import org.joda.time.{DateTime, DateTimeZone}

// $COVERAGE-OFF$
trait ScheduledTask {

  val ICT = "+07:00"
  def getCurrentTime: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))

  def run: Any
}
