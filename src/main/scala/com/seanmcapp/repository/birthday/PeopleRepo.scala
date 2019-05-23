package com.seanmcapp.repository.birthday

import com.seanmcapp.repository.DBComponent
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class People(id: Int, name: String, day: Int, month: Int)

class PeopleInfo(tag: Tag) extends Table[People](tag, "people") {
  val id = column[Int]("id", O.PrimaryKey)
  val name = column[String]("name")
  val day = column[Int]("day")
  val month = column[Int]("month")

  def * = (id, name, day, month) <> (People.tupled, People.unapply)
}

trait PeopleRepo {

  def get(day: Int, month: Int): Future[Seq[People]]

}

object PeopleRepoImpl extends TableQuery(new PeopleInfo(_)) with PeopleRepo with DBComponent {

  def get(day: Int, month: Int): Future[Seq[People]] = {
    run(this.filter(o => o.day === day && o.month === month).result)
  }

}
