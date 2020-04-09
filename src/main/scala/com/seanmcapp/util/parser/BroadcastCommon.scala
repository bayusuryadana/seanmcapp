package com.seanmcapp.util.parser

import com.seanmcapp.util.parser.decoder.JsonDecoder
import com.seanmcapp.util.parser.encoder.Encoder
import enumeratum.values.{IntEnum, IntEnumEntry}
import spray.json.{JsArray, JsNumber, JsString, JsValue, RootJsonFormat}

sealed case class BroadcastType(value: Int, code: String) extends IntEnumEntry

case object BroadcastType extends IntEnum[BroadcastType] {

  override val values = findValues

  private val broadcastTypes = values.map(channel => channel.value -> channel).toMap

  def get(code: Int): Option[BroadcastType] = broadcastTypes.get(code)

  object Unknown extends BroadcastType(0, "Unknown")

  object Text extends BroadcastType(1, "Text")

  object Image extends BroadcastType(2, "Image")

}

case class BroadcastOutput(code: Int, message: Option[String])

case class Broadcast(message: Option[String], broadcastType: BroadcastType)

trait BroadcasterCommon extends Encoder with JsonDecoder {

  implicit object broadcastTypeFormat extends RootJsonFormat[BroadcastType] {
    def write(c: BroadcastType): JsValue =
      JsArray(JsNumber(c.value), JsString(c.code))

    def read(value: JsValue): BroadcastType = value match {
      case JsArray(Vector(JsNumber(v), JsString(_))) =>
        BroadcastType.get(v.toInt).getOrElse(BroadcastType.Unknown)
      case _ => throw new Exception("BroadcastType de-serialization failed")
    }
  }

  implicit val broadcastFormat = jsonFormat2(Broadcast)

  implicit val broadcastOutputFormat = jsonFormat2(BroadcastOutput)

}
