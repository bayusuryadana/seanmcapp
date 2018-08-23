package com.seanmcapp

import com.seanmcapp.repository.Account
import com.seanmcapp.util.requestbuilder.InstagramRequest

import scalaj.http.HttpResponse

trait InstagramRequestMock extends InstagramRequest {

  override def getInstagramPageRequest(account: Account, lastId: Option[Long], csrftoken: String, sessionid: String): HttpResponse[String] = {
    HttpResponse(InputJSON.instagramResponse, 200, Map.empty)
  }

  override def getInstagramHome: HttpResponse[String] = {
    HttpResponse("", 200, Map("set-cookie" -> IndexedSeq("csrftoken=inicsrftokennya;")))
  }

  override def getInstagramAuth(username: String, password: String, csrfToken: String): HttpResponse[String] = {
    HttpResponse("", 200, Map("set-cookie" -> IndexedSeq("csrftoken=csrftokenberikutnya;", "sessionid=bisamasukneeh;")))
  }

}
