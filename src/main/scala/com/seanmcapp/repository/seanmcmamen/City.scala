package com.seanmcapp.repository.seanmcmamen

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable

// $COVERAGE-OFF$
sealed abstract class City(val i: Int) extends EnumEntry with Serializable {
  override def hashCode: Int = i
  override def equals(obj: Any): Boolean = obj match {
    case a: City => a.i == i
    case _ => false
  }
  def this() = this(hashCode)
}

object Cities extends Enum[City] {
  override def values: immutable.IndexedSeq[City] = findValues
  val fields = values.map(x => (x.i, x)).toMap

  lazy val getType: Int => City = fields.getOrElse(_, Cities.Unknown)

  def apply(value: Int): City = fields.getOrElse(value, Unknown)

  case object Unknown extends City(0)

  case object Jakarta extends City(1) 
  case object Surabaya extends City(2) 
  case object Medan extends City(3) 
  case object Semarang extends City(4)
  case object Jogjakarta extends City(5)
  case object Bandung extends City(6)
  case object Manado extends City(7)
  case object Ambon extends City(8)
}