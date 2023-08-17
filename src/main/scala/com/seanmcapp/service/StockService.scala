package com.seanmcapp.service

import com.seanmcapp.external.StockClient

class StockService(stockClient: StockClient) {
  def fetch(stockCode: String): Int = {
    if (stockCode.length == 4) 
      stockClient.fetchCurrentPrice(stockCode)
    else 
      throw new Exception (s"$stockCode is not proper stock code.")
  }
  
}
