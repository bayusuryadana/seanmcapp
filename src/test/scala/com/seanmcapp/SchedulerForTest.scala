package com.seanmcapp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder
import org.mockito.Mockito

import scala.concurrent.duration.Duration

trait SchedulerForTest {

  implicit val __actor = Mockito.mock(classOf[ActorSystem])
  implicit val __mat = Mockito.mock(classOf[ActorMaterializer])

  val startTime = 0
  val interval = Duration.Zero
  val http = Mockito.mock(classOf[HttpRequestBuilder])

}
