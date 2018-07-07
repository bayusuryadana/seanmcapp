package com.seanmcapp.startup

import com.seanmcapp.repository.postgre._

trait Injection {

  lazy val customerRepo = new CustomerRepoImpl

  lazy val photoRepo = new PhotoRepoImpl

  lazy val voteRepo = new VoteRepoImpl

}
