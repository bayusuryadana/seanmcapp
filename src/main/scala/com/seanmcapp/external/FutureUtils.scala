package com.seanmcapp.external

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

object FutureUtils {
  def await[T](f: Future[T]): T = {
    Await.result(f, FiniteDuration(30, TimeUnit.SECONDS))
  }
}
