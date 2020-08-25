package com.seanmcapp.external

case class IgrowResponse(data: Seq[IgrowData])

case class IgrowData(name: String, price: Long, stock: Int, `return`: String, contract_period: Double)

class IGrowClient(http: HttpRequestClient) {

  private val iGrowBaseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"

  def getList: IgrowResponse = {
    val response = http.sendGetRequest(iGrowBaseUrl + "/list")
    decode[IgrowResponse](response)
  }

}
