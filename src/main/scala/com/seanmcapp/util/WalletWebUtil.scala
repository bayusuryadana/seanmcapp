package com.seanmcapp.util

object WalletWebUtil {
  implicit class MapData(data: Map[String, Seq[Int]]) {
    def getWithKey(key: String): Seq[Int] = {
      data.getOrElse(key, Seq.empty[Int])
    }
  }

  implicit class DoubleHelper(d: Double) {
    def round2(): Double = "%.2f".format(d).toDouble
  }
}
