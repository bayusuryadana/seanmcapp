package com.seanmcapp.service

import com.seanmcapp.CBCServiceImpl
import org.scalatest.{AsyncWordSpec, Matchers}
import spray.json._

class WebServiceSpec extends AsyncWordSpec with Matchers with WebService with CBCServiceImpl {

  "get method, incorrect endpoint from web" in {
    val request = JsString("asdf")
    get(request).map { res =>
      res shouldBe JsString("no such method")
    }
  }

  "get method, /latest endpoint from web" in {
    val request = JsString("latest")
    get(request).map { res =>
      res.toString should include("Dwirika Widya")
    }
  }

  "get method, /random endpoint from web" in {
    val request = JsString("random")
    get(request).map { res =>
      res.toString should include("https://someurl")
    }
  }

}
