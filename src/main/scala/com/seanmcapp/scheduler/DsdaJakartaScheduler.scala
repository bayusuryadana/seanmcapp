package com.seanmcapp.scheduler

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.seanmcapp.config.SchedulerConf
import com.seanmcapp.util.parser.decoder.{DsdaJakartaDecoder}
import com.seanmcapp.util.requestbuilder.{HttpRequestBuilder, HttpRequestBuilderImpl, TelegramRequestBuilder}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.xml.XML


class DsdaJakartaScheduler(startTime: Int, interval: FiniteDuration, override val http: HttpRequestBuilder)
                    (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with DsdaJakartaDecoder with TelegramRequestBuilder {

  private val DSDA_JAKARTA_URL = "http://poskobanjirdsda.jakarta.go.id/xmldata.xml"
  private val NORMAL_STATUS = "Normal"


  override def task: Any = {
    println("=== Pintu Air Jakarta check ===")
    val response = http.sendGetRequest(DSDA_JAKARTA_URL)
    val dsdaJakartaResponse = decode(XML.loadString(response))

    val result = new java.lang.StringBuilder
    result.append(s"Seanmcapp melaporkan pintu air siaga:\n")

    dsdaJakartaResponse.waterGates
      .filter(w => !w.status.split(":")(1).trim.equalsIgnoreCase(NORMAL_STATUS))
      .map(w => result.append(s"\n${w.name.trim}: ${w.status.split(":")(1).trim}"))

    sendMessage(-1001359004262L, result.toString)
    result.toString
  }

}