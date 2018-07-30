package com.seanmcapp.API

import com.seanmcapp.DBGenerator
import com.seanmcapp.config.TelegramConf
import com.seanmcapp.startup.Injection
import org.mockito.Mockito
import org.mockito.ArgumentMatchers._
import org.scalatest.{Matchers, WordSpec}
import spray.json.JsString

import scalaj.http.{HttpRequest, HttpResponse}
import scala.concurrent.ExecutionContext.Implicits.global

class WebAPISpec extends WordSpec with Matchers with Injection {

  DBGenerator.generate

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

  /*
  "broadcast" should {
    val key = TelegramConf().key
    val request = JsString("broadcast")
    val httpRequestMock = Mockito.mock(classOf[HttpRequest])
    Mockito.when(webAPI.getTelegramSendMessege(anyLong(), anyString())).thenReturn(httpRequestMock)
    val httpResponseMock = Mockito.mock(classOf[HttpResponse[String]])
    Mockito.when(httpRequestMock.asString).thenReturn(httpResponseMock)
    Mockito.when(httpResponseMock.isSuccess).thenReturn(true)

    "succeed broadcast" in {
      val input = JsString("{\"recipient\": 274852283, \"message\": \"hi :)\", \"key\": \"" + key + "\"}")
      webAPI.flow(request, input).map { res =>
        res.toString shouldBe "true"
      }
    }
  }
  */

}
