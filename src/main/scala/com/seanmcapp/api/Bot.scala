package com.seanmcapp.api

import com.seanmcapp.repository.{Customer, CustomerRepo, Vote, VoteRepo}

trait Bot {

  val customerRepo: CustomerRepo
  val voteRepo: VoteRepo

  def subscribe(customerDefault: Customer): Unit = {
    customerRepo.update(customerDefault.copy(isSubscribed = true))
  }

  def resetCustomer(customerDefault: Customer): Unit = {
    customerRepo.update(customerDefault)
  }

  def vote(vote: Vote): Unit = {
    voteRepo.update(vote)
  }

}
