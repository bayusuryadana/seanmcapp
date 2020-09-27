package com.seanmcapp.external

import com.seanmcapp.service.{InstagramAccountResponse, InstagramRequestParameter, InstagramResponse}
import io.circe.syntax._

class InstagramClient(http: HttpRequestClient) {

  def getAccountResponse(accountId: String): InstagramAccountResponse = {
    val url = s"https://www.instagram.com/$accountId/?__a=1"
    val httpResponse = http.sendGetRequest(url)
    decode[InstagramAccountResponse](httpResponse)
  }

  def getPhotos(userId: String, endCursor: Option[String], sessionId: String): InstagramResponse = {
    val numberOfBatch = 50
    val params = InstagramRequestParameter(userId, numberOfBatch, endCursor).asJson.encode
    val url = s"https://www.instagram.com/graphql/query/?query_hash=18a7b935ab438c4514b1f742d8fa07a7&variables=$params"
    val headers = Some(HeaderMap(Map("cookie" -> s"sessionid=$sessionId")))
    val httpResponse = http.sendRequest(url, headers = headers)
    decode[InstagramResponse](httpResponse)
  }

}
