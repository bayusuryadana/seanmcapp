package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class HeroAttribute(id: Int, baseHealth: Int, baseHealthRegen: Double, baseMana: Int, baseManaRegen: Double,
                         baseArmor: Int, baseMR: Int, baseAttackMin: Int, baseAttackMax: Int, baseStr: Int, baseAgi: Int, baseInt: Int,
                         strGain: Double, agiGain: Double, intGain: Double, attackRange: Int, projectileSpeed: Int, attackRate: Double,
                         moveSpeed: Int, turnRate: Double, cmEnabled: Boolean)

class HeroAttributeInfo(tag: Tag) extends Table[HeroAttribute](tag, "hero_attributes") {
  val id = column[Int]("id", O.PrimaryKey)
  val baseHealth = column[Int]("base_health")
  val baseHealthRegen = column[Double]("base_health_regen")
  val baseMana = column[Int]("base_mana")
  val baseManaRegen = column[Double]("base_mana_regen")
  val baseArmor = column[Int]("base_armor")
  val baseMR = column[Int]("base_mr")
  val baseAttackMin = column[Int]("base_attack_min")
  val baseAttackMax = column[Int]("base_attack_max")
  val baseStr = column[Int]("base_str")
  val baseAgi = column[Int]("base_agi")
  val baseInt = column[Int]("base_int")
  val strGain = column[Double]("str_gain")
  val agiGain = column[Double]("agi_gain")
  val intGain = column[Double]("int_gain")
  val attackRange = column[Int]("attack_range")
  val projectileSpeed = column[Int]("projectile_speed")
  val attackRate = column[Double]("attack_rate")
  val moveSpeed = column[Int]("move_speed")
  val turnRate = column[Double]("turn_rate")
  val cmEnabled = column[Boolean]("cm_enabled")

  def * = (id, baseHealth, baseHealthRegen, baseMana, baseManaRegen, baseArmor, baseMR, baseAttackMin, baseAttackMax,
    baseStr, baseAgi, baseInt, strGain, agiGain, intGain, attackRange, projectileSpeed, attackRate, moveSpeed, turnRate,
    cmEnabled) <> (HeroAttribute.tupled, HeroAttribute.unapply)
}

trait HeroAttributeRepo {

  def getAll: Future[Seq[HeroAttribute]]

  def get(id: Int): Future[Option[HeroAttribute]]

  def insertOrUpdate(attributes: Seq[HeroAttribute]): Seq[Future[Int]]

}

object HeroAttributeRepoImpl extends TableQuery(new HeroAttributeInfo(_)) with HeroAttributeRepo with DBComponent {

  def getAll: Future[Seq[HeroAttribute]] = {
    run(this.result)
  }

  def get(id: Int): Future[Option[HeroAttribute]] = {
    run(this.filter(_.id === id).result.headOption)
  }

  def insertOrUpdate(attributes: Seq[HeroAttribute]): Seq[Future[Int]] = attributes.map { attribute =>
    // try insert first
    run((this += attribute).asTry).flatMap {
      case Failure(ex) =>
        // else try update
        run(this.filter(_.id === attribute.id).update(attribute))
      case Success(value) =>
        Future.successful(value)
    }
  }

}