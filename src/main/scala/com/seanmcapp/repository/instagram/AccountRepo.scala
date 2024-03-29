package com.seanmcapp.repository.instagram

import slick.jdbc.PostgresProfile.api._
import com.seanmcapp.repository.DBComponent
import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable
import scala.concurrent.Future

case class Account(id: String, alias: String, groupType: AccountGroupType)

object AccountUtil {
  def apply(a:(String, String, Int)) = Account(a._1, a._2, AccountGroupTypes.apply(a._3))
  def unapply(a: Account) = Some((a.id, a.alias, a.groupType.i))
}

class AccountInfo (tag: Tag) extends Table[Account](tag, "accounts") {
  val id = column[String]("id", O.PrimaryKey)
  val alias = column[String]("alias")
  val groupType = column[Int]("group_type")

  def * = (id, alias, groupType) <> (AccountUtil.apply, AccountUtil.unapply)
}

trait AccountRepo {

  def getAll(groupType: AccountGroupType): Future[Seq[Account]]

}

object AccountRepoImpl extends TableQuery(new AccountInfo(_)) with AccountRepo with DBComponent {

  def getAll(groupType: AccountGroupType): Future[Seq[Account]] = run(this.filter(_.groupType === groupType.i).result)

}

// $COVERAGE-OFF$
sealed abstract class AccountGroupType(val i: Int) extends EnumEntry with Serializable {
  override def hashCode: Int = i
  override def equals(obj: Any): Boolean = obj match {
    case a: AccountGroupType => a.i == i
    case _ => false
  }
  def this() = this(hashCode)
}

object AccountGroupTypes extends Enum[AccountGroupType] {
  override def values: immutable.IndexedSeq[AccountGroupType] = findValues
  val fields = values.map(x => (x.i, x)).toMap

  lazy val getType: Int => AccountGroupType = fields.getOrElse(_, AccountGroupTypes.Unknown)

  def apply(value: Int): AccountGroupType = fields.getOrElse(value, Unknown)

  case object Deactivated extends AccountGroupType(-1)
  case object Unknown extends AccountGroupType(0)
  
  case object Stalker extends AccountGroupType(1) // to Group, required login
  case object StalkerSpecial extends AccountGroupType(2) // to Personal, no login required
  case object CBC extends AccountGroupType(3) // to DB, required login
}