package com.seanmcapp.util

object WalletWebUtil {
  implicit class MapData(data: Map[String, Seq[Int]]) {
    def getWithKey(key: String): Seq[Int] = {
      data.getOrElse(key, Seq.empty[Int])
    }
  }
}
