package com.seanmcapp.API

import com.seanmcapp.InjectionTest
import com.seanmcapp.config.TelegramConf
import org.scalatest.{AsyncWordSpec, Matchers}
import spray.json.JsString

class WebAPISpec extends AsyncWordSpec with Matchers with InjectionTest {

  "get latest" in {
    val request = JsString("latest")
    webAPI.flow(request).map { res =>
      res.toString shouldBe "{\"thumbnailSrc\":\"https://scontent-iad3-1.cdninstagram.com/vp/7c657bd346591a294cf51bd022285230/5B3FADF9/t51.2885-15/s640x640/sh0.08/e35/c0.96.764.764/28764036_154095071930810_8807155097025904640_n.jpg\"," +
        "\"id\":\"1734075033692644433\"," +
        "\"date\":1520937874," +
        "\"caption\":\"Lovita Soraya. FIB'13\"," +
        "\"account\":\"ui.cantik\"" +
        "}"
    }
  }

  "broadcast" should {
    import spray.json._
    val key = TelegramConf().key
    val request = JsString("broadcast")

    "succeed multiple" in {
      val input = s"""{"recipient":0,"message":"hi :)","key":"$key"}""".parseJson
      webAPI.flow(request, input).map { res =>
        res.toString should equal ("true")
      }
    }

    "succeed single" in {
      val input = s"""{"recipient":274852283,"message":"hi :)","key":"$key"}""".parseJson
      webAPI.flow(request, input).map { res =>
        res.toString should equal ("true")
      }
    }

    "wrong key" in {
      val input = s"""{"recipient":274852283,"message":"hi :)","key":"ngasal broh"}""".parseJson
      webAPI.flow(request, input).map { res =>
        res should equal (JsString("wrong key"))
      }
    }
  }

}
