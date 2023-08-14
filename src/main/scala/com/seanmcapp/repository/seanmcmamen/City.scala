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

  lazy val getType: Int => City = fields.getOrElse(_, Cities.Unmapped)

  def apply(value: Int): City = fields.getOrElse(value, Unmapped)

  case object Unmapped extends City(0)

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
  case object Semarang extends City(21)
  case object Bandung extends City(22)
  case object Manado extends City(23)
  case object BandaAceh extends City(24)
  case object Medan extends City(25)
  case object Solo extends City(26)
  case object Madura extends City(27)
  case object Cirebon extends City(28)
  case object TangerangSelatan extends City(29)
  case object Malang extends City(30)
  case object Kediri extends City(31)
  case object Purwakarta  extends City(32)
  case object Tegal extends City(33)
  case object Balikpapan extends City(34)
  case object Sukabumi extends City(35)
  case object Lombok extends City(36)
  case object Makassar extends City(37)
  case object Purwokerto extends City(38)
  case object Penang extends City(39)
  case object KualaLumpur extends City(40)
  case object Karawang extends City(41)
  case object Serang extends City(42)
  case object Cilegon extends City(43)
  case object Bekasi extends City(44)
  case object Tasikmalaya extends City(45)
  case object Subang extends City(46)
  case object Jambi extends City(47)
  case object Bengkulu extends City(48)
  case object Kupang extends City(49)
  case object Ternate extends City(50)
  case object Ambon extends City(51)
  case object Depok extends City(52)
  case object Karangasem extends City(53)
  case object Sorong extends City(54)
  case object Jayapura extends City(55)
}
