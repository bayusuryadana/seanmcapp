package com.seanmcapp.repository

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class ConfigurationData(key: String, value: String)

class ConfigurationInfo(tag: Tag) extends Table[ConfigurationData](tag, "configurations") {
  val key = column[String]("key", O.PrimaryKey)
  val value = column[String]("value")

  def * = (key, value) <> (ConfigurationData.tupled, ConfigurationData.unapply)
}

trait ConfigurationRepo {

  def get(key: String): Future[Option[ConfigurationData]]

}

object ConfigurationRepoImpl extends TableQuery(new ConfigurationInfo(_)) with ConfigurationRepo with DBComponent {

  def get(key: String): Future[Option[ConfigurationData]] = run(this.filter(_.key === key).result.headOption)

}
