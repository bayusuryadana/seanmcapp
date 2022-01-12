package com.seanmcapp.repository.instagram

import slick.jdbc.PostgresProfile.api._
import com.seanmcapp.repository.DBComponent
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable
import scala.concurrent.Future

case class Account(id: String, alias: String, isPrivate: Boolean, groupType: AccountGroupType)

object AccountUtil {
  def apply(a:(String, String, Boolean, Int)) = Account(a._1, a._2, a._3, AccountGroupType.apply(a._4))
  def unapply(a: Account) = Some((a.id, a.alias, a.isPrivate, a.groupType.i))
}

class AccountInfo (tag: Tag) extends Table[Account](tag, "accounts") {
  val id = column[String]("id", O.PrimaryKey)
  val alias = column[String]("alias")
  val isPrivate = column[Boolean]("is_private")
  val groupType = column[Int]("group_type")

  def * = (id, alias, isPrivate, groupType) <> (AccountUtil.apply, AccountUtil.unapply)
}

trait AccountRepo {

  def getAll(): Future[Seq[Account]]

}

object AccountRepoImpl extends TableQuery(new AccountInfo(_)) with AccountRepo with DBComponent {

  def getAll(): Future[Seq[Account]] = run(this.result)

}

sealed abstract class AccountGroupType(val i: Int) extends EnumEntry with Serializable {
  override def hashCode: Int = i
  override def equals(obj: Any): Boolean = obj match {
    case a: AccountGroupType => a.i == i
    case _ => false
  }
  def this() = this(hashCode)
}

object AccountGroupType extends Enum[AccountGroupType] {
  override def values: immutable.IndexedSeq[AccountGroupType] = findValues
  val fields = values.map(x => (x.i, x)).toMap

  lazy val getType: Int => AccountGroupType = fields.getOrElse(_, AccountGroupType.Unknown)

  def apply(value: Int): AccountGroupType = fields.getOrElse(value, Unknown)

  case object Unknown extends AccountGroupType(0)
  case object Lantai5 extends AccountGroupType(1)
  case object Special extends AccountGroupType(2)
}