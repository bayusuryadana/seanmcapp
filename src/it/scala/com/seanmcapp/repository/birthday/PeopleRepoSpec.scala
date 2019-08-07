package com.seanmcapp.repository.birthday

import org.scalatest.{AsyncWordSpec, Matchers}

class PeopleRepoSpec extends AsyncWordSpec with Matchers {

  "should return people today's birthday" in {
    val response = PeopleRepoImpl.get(6,8)
    response.map { res =>
      res shouldEqual Seq(People(25,"Hafiyyan Sayyid Fadhlillah",6,8))
    }
  }

  "should not return people today's birthday" in {
    val response = PeopleRepoImpl.get(1,1)
    response.map { res =>
      res shouldEqual Seq.empty
    }
  }

}
