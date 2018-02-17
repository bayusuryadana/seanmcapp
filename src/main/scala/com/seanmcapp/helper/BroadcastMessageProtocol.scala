package com.seanmcapp.helper

import com.seanmcapp.model.BroadcastMessage
import spray.json._

object BroadcastMessageProtocol extends DefaultJsonProtocol {

  implicit object BroadcastMessageFormat extends RootJsonFormat[BroadcastMessage] {
    override def write(obj: BroadcastMessage) = {
      JsObject(
        "message" -> JsString(obj.message),
        "key" -> JsString(obj.key)
      )
    }

    override def read(value: JsValue): BroadcastMessage = {
      value.asJsObject.getFields("message", "key") match {
        case Seq(JsString(message), JsString(key)) => BroadcastMessage(message, key)
        case _ => throw new DeserializationException("failed to deserialize BroadcastMessage")
      }
    }
  }

}
