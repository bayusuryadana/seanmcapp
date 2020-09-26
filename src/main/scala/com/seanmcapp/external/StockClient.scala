package com.seanmcapp.external

import java.util.concurrent.TimeUnit

import com.seanmcapp.util.MemoryCache
import scalacache.Cache
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._

import scala.concurrent.duration.Duration

class StockClient(http: HttpRequestClient) extends MemoryCache {

  implicit val stockCache: Cache[StockResponse] = createCache[StockResponse]
  private val duration = Duration(15, TimeUnit.MINUTES)

  def getStockResult(stockIdList: Seq[String]): StockResponse = {
    memoizeSync(Some(duration)) {
      val url = s"https://bloomberg-market-and-financial-news.p.rapidapi.com/market/get-chart?" +
        s"id=${stockIdList.foldLeft("")((s, c) => s"$s$c:IJ,").dropRight(1)}&interval=d1"
      val headers = HeaderMap(Map(
        "x-rapidapi-host" -> "bloomberg-market-and-financial-news.p.rapidapi.com",
        "x-rapidapi-key" -> "1917b0cf26msh7824c7e68f8c4cap16c0a7jsn3583cd703049"
      ))
      val response = http.sendRequest(url, headers = Some(headers))
      decode[StockResponse](response)
    }
  }

}

case class StockResponse(result: Map[String, StockDetail])
case class StockDetail(ticks: List[StockTick])
case class StockTick(time: Long, close: Int, volume: Long)
