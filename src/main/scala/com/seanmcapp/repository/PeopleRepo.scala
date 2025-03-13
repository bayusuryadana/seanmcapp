package com.seanmcapp.repository

import anorm.{Macro, RowParser, SqlStringInterpolation}

import scala.concurrent.Future

case class People(id: Int, name: String, day: Int, month: Int)

trait PeopleRepo {
  def get(day: Int, month: Int): Future[Seq[People]]
}

class PeopleRepoImpl(client: DatabaseClient) extends PeopleRepo {

  val parser: RowParser[People] = Macro.namedParser[People]

  def get(day: Int, month: Int): Future[Seq[People]] = {
    client.withConnection { implicit conn =>
      SQL"SELECT name FROM users".as(parser.*)
    }
  }
}