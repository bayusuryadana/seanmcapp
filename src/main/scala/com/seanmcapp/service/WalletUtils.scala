package com.seanmcapp.service

import java.text.NumberFormat

object WalletUtils {

  implicit class Formatter(in: Int) {
    def formatNumber: String = {
      val formatter = NumberFormat.getIntegerInstance
      formatter.format(in)
    }
  }

  implicit class DateCalculator(date: Int) {
    def adjustDate: Int = {
      date % 100 match {
        case 13 =>(date / 100 + 1) * 100 + 1
        case 0 => (date / 100 - 1) * 100 + 12
        case _ => date
      }
    }
  }

}
