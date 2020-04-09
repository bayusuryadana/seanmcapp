package com.seanmcapp.util.parser

import com.seanmcapp.util.parser.decoder.JsonDecoder
import com.seanmcapp.util.parser.encoder.Encoder
import enumeratum.values.{IntEnum, IntEnumEntry}

sealed abstract class BroadcastType(val value: Int, val code: String) extends IntEnumEntry

case object BroadcastType extends IntEnum[BroadcastType] {

  override val values = findValues

  object Unknown extends BroadcastType(0, "Unknown")

  object Text extends BroadcastType(1, "Text")

  object Image extends BroadcastType(2, "Image")
}

case class BroadcastOutput(code: Int, message: Option[String])

case class Broadcast(message: Option[String], broadcastType: BroadcastType)

trait BroadcasterCommon extends Encoder with JsonDecoder {

  implicit val broadcastFormat = jsonFormat2(Broadcast)

  implicit val broadcastOutputFormat = jsonFormat2(BroadcastOutput)

}
