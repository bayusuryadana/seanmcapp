package com.seanmcapp.util

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

sealed abstract class ChatIdType(val i: Long) extends EnumEntry with Serializable {
  override def hashCode: Long = i
  override def equals(obj: Any): Boolean = obj match {
    case a: ChatIdType => a.i == i
    case _ => false
  }
  def this() = this(hashCode)
}

object ChatIdType extends Enum[ChatIdType] {
  override def values: immutable.IndexedSeq[ChatIdType] = findValues
  val fields = values.map(x => (x.i, x)).toMap

  lazy val getType: Long => ChatIdType = fields.getOrElse(_, ChatIdType.Unknown)

  def apply(value: Long): ChatIdType = fields.getOrElse(value, Unknown)

  case object Unknown extends ChatIdType(0)
  case object Personal extends ChatIdType(1)
  case object Group extends ChatIdType(-1001359004262L)
}