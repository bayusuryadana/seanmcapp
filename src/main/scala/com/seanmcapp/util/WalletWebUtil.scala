package com.seanmcapp.util

object WalletWebUtil {
  implicit class Expense(data: Map[String, Map[String, Seq[Int]]]) {
    def getExpense(currency: String, category: String): Seq[Int] = {
      data.get(currency).flatMap(_.get(category)).getOrElse(Seq.empty[Int])
    }
  }

  implicit class MapData(data: Map[String, Seq[Int]]) {
    def getWithKey(key: String): Seq[Int] = {
      data.getOrElse(key, Seq.empty[Int])
    }
  }

  def colSum[T: Numeric](m: Iterable[Iterable[T]]) : List[T] = {
    val filtered = m.filterNot(_.isEmpty)
    if(filtered.isEmpty)
      List.empty[T]
    else
      filtered.map(_.head).sum :: colSum(filtered.map(_.tail))
  }
}
