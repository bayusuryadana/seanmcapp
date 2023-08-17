package com.seanmcapp.external

case class StockMeta(regularMarketPrice: Int)
case class StockResult(meta: StockMeta)
case class StockChart(result: Seq[StockResult])
case class StockResponse(chart: StockChart)
class StockClient(httpRequestClient: HttpRequestClient) {

  def fetchCurrentPrice(stockCode: String): Int = {
    val url = s"https://query1.finance.yahoo.com/v8/finance/chart/$stockCode.JK"
    val response = httpRequestClient.sendGetRequest(url)
    decode[StockResponse](response).chart.result.headOption.map(_.meta.regularMarketPrice)
      .getOrElse(throw new Exception("price not found"))
  }
  
}
