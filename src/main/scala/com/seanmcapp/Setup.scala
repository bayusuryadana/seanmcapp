package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import com.seanmcapp.external._
import com.seanmcapp.repository.seanmcwallet.Wallet
import io.circe.syntax._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.Try

// $COVERAGE-OFF$
class Setup(implicit system: ActorSystem, ec: ExecutionContext) extends Directives with Injection {

  val discord = new DiscordClient(cbcService, hadithService).run()

  val route: server.Route = List(

    // webhook
    post((path("webhook") & entity(as[String])) { payload =>
      val telegramUpdate = decode[TelegramUpdate](payload)
      complete(telegramWebhookService.receive(telegramUpdate).map(_.map(_.asJson.encode)))
    }),

    // cbc API
    get(path("cbc" / "random")(complete(cbcService.random.map(_.map(_.asJson.encode))))),

    // dota APP
    get(path("dota")(complete(dotaService.home.map(_.asJson.encode)))),

    // wallet
    get {
      (path("wallet") & headerValue(getHeader("secretkey"))) { secretKey =>
        complete(walletService.dashboard(secretKey).asJson.encode)
      } ~ (path("wallet" / "data" / Remaining.?) & headerValue(getHeader("secretkey"))) { (date, secretKey) =>
        complete(walletService.data(secretKey, date.flatMap(d => Try(d.toInt).toOption)).asJson.encode)
      } ~ (path("wallet" / "amartha" ) & headerValue(getHeader("secretkey"))) { secretKey =>
        complete(walletService.amartha(secretKey).asJson.encode)
      }
    },
    post {
      (path("wallet") & headerValue(getHeader("secretkey")) & entity(as[String])) { (secretKey, payload) =>
        val wallet = decode[Wallet](payload)
        complete(walletService.insert(wallet)(secretKey).toString)
      }
    },
    put {
      (path("wallet") & headerValue(getHeader("secretkey")) & entity(as[String])) { (secretKey, payload) =>
        val wallet = decode[Wallet](payload)
        complete(walletService.update(wallet)(secretKey).toString)
      }
    },
    delete {
      (path("wallet" / Remaining) & headerValue(getHeader("secretkey"))) { (id, secretKey) =>
        complete(walletService.delete(id.toInt)(secretKey).toString)
      }
    },

    // broadcast
    toStrictEntity(3.seconds) {
      post((path("broadcast") & headerValue(getHeader("secretkey")) & fileUpload("photo") & formFieldMap) {
        case (secretKey, (_, byteSource), formFields) =>
          complete(broadcastService.broadcastWithPhoto(byteSource, formFields)(system, secretKey).map(_.asJson.encode))
      })
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

  val scheduleList: List[Scheduler] = List(
    /**
      * this is not using normal cron convention format.
      * This cron4s expressions go from seconds to day of week in the following order:
      * (Seconds, Minutes, Hour Of Day, Day Of Month, Month, Day Of Week)
      */
    new Scheduler(warmupDBService, "0 * * * * ?", false),
    new Scheduler(warmupDBService, "*/5 * * * * ?", false),
    new Scheduler(dotaService, "0 0 2 * * ?"),
    new Scheduler(birthdayService, "0 0 6 * * ?"),
    new Scheduler(iGrowService, "0 0 6 * * ?"),
    new Scheduler(airVisualService, "0 0 8 * * ?"),
    new Scheduler(airVisualService, "0 0 17 * * ?"),
    new Scheduler(nCovService, "0 0 20 * * ?"),
    new Scheduler(dsdaJakartaService, "0 0 0 * * ?"),
    new Scheduler(amarthaService, "0 0 18 * * ?"),
    new Scheduler(instagramService, "0 0 10 * * ?"),
    new Scheduler(instagramStoryService, "0 0 * * * ?"),
    new Scheduler(newsService, "0 0 6 * * ?"),
  )

}
