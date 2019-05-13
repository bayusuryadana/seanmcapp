package com.seanmcapp.service

import com.seanmcapp.util.parser.IgrowResponse
import scalaj.http.Http
import spray.json._

trait IgrowService {

  private val baseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"

  def fetch: IgrowResponse = {
    import com.seanmcapp.util.parser.IgrowJson._
    Http(baseUrl + "/list").asString.body.parseJson.convertTo[IgrowResponse]
  }

}
