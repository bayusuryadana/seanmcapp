package com.seanmcapp.service

import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, TelegramRequestBuilder}

class BroadcastService(override val http: HttpRequestBuilder) extends TelegramRequestBuilder{

}
