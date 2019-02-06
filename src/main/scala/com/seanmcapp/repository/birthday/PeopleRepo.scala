package com.seanmcapp.repository.birthday

import scala.concurrent.Future

case class People(id: Int, name: String, day: Int, month: Int)

trait PeopleRepo {

  def get(day: Int, month: Int): Future[Seq[People]]

}
