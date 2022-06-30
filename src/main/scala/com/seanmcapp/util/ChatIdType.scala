package com.seanmcapp.util

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed abstract class ChatIdType(val i: Long) extends EnumEntry with Serializable {
  override def equals(obj: Any): Boolean = obj match {
    case a: ChatIdType => a.i == i
    case _ => false
  }
  def this() = this(hashCode)
}

object ChatIdTypes extends Enum[ChatIdType] {
  override def values: immutable.IndexedSeq[ChatIdType] = findValues
  val fields = values.map(x => (x.i, x)).toMap

  lazy val getType: Long => ChatIdType = fields.getOrElse(_, ChatIdTypes.Unknown)

  def apply(value: Long): ChatIdType = fields.getOrElse(value, Unknown)

  case object Unknown extends ChatIdType(0L)
  case object Personal extends ChatIdType(274852283L)
  case object Group extends ChatIdType(-1001359004262L)
}