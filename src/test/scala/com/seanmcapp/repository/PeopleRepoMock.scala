package com.seanmcapp.repository

import com.seanmcapp.repository.{People, PeopleRepo}

import scala.concurrent.Future

object PeopleRepoMock extends PeopleRepo {

  private val peopleList = Seq(
    People(1, "Bayu Suryadana", 17, 1),
    People(2, "Satrio Gumilar", 23, 11),
    People(3, "Selena Dennysal", 8, 10),
    People(4, "Andreas Jonanisco", 2, 2)
  )

  override def get(day: Int, month: Int): Future[Seq[People]] = Future.successful(peopleList.filter(p => p.day == day && p.month == month))

}
