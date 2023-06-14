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
  case object Bali extends City(3)
  case object Pekanbaru extends City(4)
  case object Batam extends City(5)
  case object Palembang extends City(6)
  case object BandarLampung extends City(7)
  case object Bangka extends City(8)
  case object Belitung extends City(9)
  case object Padang extends City(10)
  case object Nias extends City(11)
  case object Bukittinggi extends City(12)
  case object Singkawang extends City(13)
  case object Palangkaraya extends City(14)
  case object Banjarmasin extends City(15)
  case object Samarinda extends City(16)
  case object Tangerang extends City(17)
  case object Yogyakarta extends City(18)
  case object Pontianak extends City(19)
  case object Bogor extends City(20)
}