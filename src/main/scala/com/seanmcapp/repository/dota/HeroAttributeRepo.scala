package com.seanmcapp.repository.dota

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class HeroAttribute(id: Int, base_health: Int, base_health_regen: Double, base_mana: Int, base_mana_regen: Double,
                         base_armor: Double, base_mr: Int, base_attack_min: Int, base_attack_max: Int, base_str: Int, base_agi: Int, base_int: Int,
                         str_gain: Double, agi_gain: Double, int_gain: Double, attack_range: Int, projectile_speed: Int, attack_rate: Double,
                         move_speed: Int, turn_rate: Double, cm_enabled: Boolean)

object HeroAttribute {

  def dummy(id: Int): HeroAttribute = {
    HeroAttribute(id, 0, 0d, 0, 0d, 0, 0, 0, 0, 0, 0, 0, 0d, 0d, 0d, 0, 0, 0d, 0, 0d, false)
  }

  def tupled = (HeroAttribute.apply _).tupled

}

class HeroAttributeInfo(tag: Tag) extends Table[HeroAttribute](tag, "hero_attributes") {
  val id = column[Int]("id", O.PrimaryKey)
  val baseHealth = column[Int]("base_health")
  val baseHealthRegen = column[Double]("base_health_regen")
  val baseMana = column[Int]("base_mana")
  val baseManaRegen = column[Double]("base_mana_regen")
  val baseArmor = column[Double]("base_armor") // in postgres is int
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

  def insertOrUpdate(attributes: Seq[HeroAttribute]): Seq[Future[Int]]

}

object HeroAttributeRepoImpl extends TableQuery(new HeroAttributeInfo(_)) with HeroAttributeRepo with DBComponent {

  def getAll: Future[Seq[HeroAttribute]] = {
    run(this.result)
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