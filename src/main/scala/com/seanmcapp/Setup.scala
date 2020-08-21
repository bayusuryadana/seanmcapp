package com.seanmcapp

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import com.seanmcapp.repository.seanmcwallet.Wallet
import io.circe.syntax._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class Setup(implicit system: ActorSystem, ec: ExecutionContext) extends Directives with Injection {

  import com.seanmcapp.external._
  val route: server.Route = List(

    // cbc API
    get(path("cbc" / "random")(complete(cbcService.random.map(_.map(_.asJson.asString))))),
    post((path("cbc" / "webhook") & entity(as[String])) { payload =>
      val telegramUpdate = decode[TelegramUpdate](payload)
      complete(cbcService.randomFlow(telegramUpdate).map(_.map(_.asJson.asString)))
    }),

    // instagram fetcher API
    get(path("instagram" / Remaining)(cookie => complete(instagramService.fetch(cookie).map(_.asJson.asString)))),

    // dota APP
    get(path("dota")(complete(dotaService.home.map(_.asJson.asString)))),

    // wallet
    get {
      (path("wallet") & headerValue(getHeader("secretkey"))) { secretKey =>
        complete(walletService.getAll(secretKey).map(_.asJson.asString))
      }
    },
    post {
      (path("wallet") & headerValue(getHeader("secretkey")) & entity(as[String])) { (secretKey, payload) =>
        val wallet = decode[Wallet](payload)
        complete(walletService.insert(wallet)(secretKey).map(_.asJson.asString))
      }
    },
    put {
      (path("wallet") & headerValue(getHeader("secretkey")) & entity(as[String])) { (secretKey, payload) =>
        val wallet = decode[Wallet](payload)
        complete(walletService.update(wallet)(secretKey).map(_.asJson.asString))
      }
    },
    delete {
      (path("wallet" / Remaining) & headerValue(getHeader("secretkey"))) { (id, secretKey) =>
        complete(walletService.delete(id.toInt)(secretKey).map(_.asJson.asString))
      }
    },

    // broadcast
    toStrictEntity(3.seconds) {
      post((path("broadcast") & headerValue(getHeader("secretkey")) & fileUpload("photo") & formFieldMap) {
        case (secretKey, (_, byteSource), formFields) =>
          complete(broadcastService.broadcastWithPhoto(byteSource, formFields)(system, secretKey).map(_.asJson.asString))
      })
    },

    // amartha
    get {
      (path("amartha") & headerValue(getHeader("username")) & headerValue(getHeader("password"))) { (username, password) =>
        complete(amarthaService.processResult(username, password).asJson.asString.getOrElse(throw new NullPointerException("[ERROR] Error while parsing Amartha result")))
      }
    },

    // homepage
    get(path("")(complete("Life is a gift, keep smiling and giving goodness !")))

  ).reduce{ (a,b) => a~b }

  private def getHeader(key: String): HttpHeader => Option[String] = {
    (httpHeader: HttpHeader) => {
      HttpHeader.unapply(httpHeader) match {
        case Some((k, v)) if k == key => Some(v)
        case _ => None
      }
    }
  }

  // $COVERAGE-OFF$
  private val everyDay = Some(Duration(1, TimeUnit.DAYS))

  val scheduleList: List[Scheduler] = List(
    new Scheduler(-1, None, warmupDBService),
    new Scheduler(-5, None, warmupDBService),
    new Scheduler(3, everyDay, dotaService),
    new Scheduler(6, everyDay, birthdayService),
    new Scheduler(6, everyDay, iGrowService),
    new Scheduler(8, everyDay, airVisualService),
    new Scheduler(17, everyDay, airVisualService),
    new Scheduler(20, everyDay, nCovService),
    new Scheduler(0, everyDay, dsdaJakartaService),
    new Scheduler(7, everyDay, amarthaService),
  )
  // $COVERAGE-ON$

}
