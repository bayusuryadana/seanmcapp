package com.seanmcapp.external

import com.seanmcapp.service.{InstagramAccountResponse, InstagramResponse}

class InstagramClient(http: HttpRequestClient) {

  def getAccountResponse(accountId: String, cookie: String): InstagramAccountResponse = {
    val initUrl = "https://www.instagram.com/" + accountId + "/?__a=1"
    val headers = Some(HeaderMap(Map("cookie" -> cookie)))
    val httpResponse = http.sendRequest(initUrl, headers = headers)
    decode[InstagramAccountResponse](httpResponse)
  }

  def getPhotos(userId: Long, endCursor: Option[String], cookie: String): InstagramResponse = {
    val fetchUrl = "https://www.instagram.com/graphql/query/?query_id=17888483320059182&id=<user_id>&first=50&after=<end_cursor>"
    val url = fetchUrl.replace("<user_id>", userId.toString).replace("<end_cursor>", endCursor.getOrElse(""))
    val headers = Some(HeaderMap(Map("cookie" -> cookie)))
    val httpResponse = http.sendRequest(url, headers = headers)
    decode[InstagramResponse](httpResponse)
  }

}
