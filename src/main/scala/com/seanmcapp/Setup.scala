package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpHeader, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import com.seanmcapp.external._
import io.circe.syntax._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import scala.util.Try

// $COVERAGE-OFF$
class Setup(implicit system: ActorSystem, ec: ExecutionContext) extends Directives with Injection with Session {

  private val utf8 = ContentTypes.`text/html(UTF-8)`

  val discord = new DiscordClient(cbcService, hadithService).run()

  val route: server.Route = List(

    /////////// WEBHOOK ///////////
    post((path("webhook") & entity(as[String])) { payload =>
      val telegramUpdate = decode[TelegramUpdate](payload)
      complete(telegramWebhookService.receive(telegramUpdate).map(_.map(_.asJson.encode)))
    }),

    /////////// API ///////////
    get(path( "api" / "instagram" / Remaining) { session =>
      if (session == "null") complete(instagramService.startFetching().map(_.asJson.encode))
      else complete(instagramService.startFetching(Some(session)).map(_.asJson.encode))
    }),
    get(path( "api" / "instastory" / Remaining) { session =>
      if (session == "null") complete(instagramStoryService.fetch().map(_.asJson.encode))
      else complete(instagramStoryService.fetch(Some(session)).map(_.asJson.encode))
    }),
    get(path( "api" / "metadota" )(complete(dotaService.run.map(_.asJson.encode)))),

    toStrictEntity(3.seconds) {
      post((path("broadcast") & headerValue(getHeader("secretkey")) & fileUpload("photo") & formFieldMap) {
        case (secretKey, (_, byteSource), formFields) =>
          println(s"Receive broadcast with formFields: $formFields")
          complete(broadcastService.broadcastWithPhoto(byteSource, formFields)(system, secretKey).map(_.asJson.encode))
      })
    },

    /////////// WEB ///////////
    get(path("dota")(complete(dotaService.home.map(HttpEntity(utf8, _))))),

    pathPrefix("wallet") {
      post {
        (pathPrefix("do_login") & formField('secretKey)) { body =>
          if (walletService.login(body)) {
            setSession(body)(_.redirect("/wallet", StatusCodes.Found))
          } else {
            redirect("/wallet/login", StatusCodes.SeeOther)
          }
        }
      } ~
      get {
        pathPrefix("login") {
          complete(HttpEntity(utf8, com.seanmcapp.wallet.html.login().body))
        } ~
        validateSession { session =>
          pathEndOrSingleSlash {
            val dashboardView = walletService.dashboard(session)
            _.complete(HttpEntity(utf8, com.seanmcapp.wallet.html.dashboard(dashboardView).body))
          } ~ (pathPrefix("data") & parameters('date.?)) { date =>
            val dataView = walletService.data(session, date.flatMap(d => Try(d.toInt).toOption))
            _.complete(HttpEntity(utf8, com.seanmcapp.wallet.html.data(dataView).body))
          } ~ pathPrefix("do_logout") {
            invalidateSession(_.redirect("/wallet/login", StatusCodes.Found))
          }
        }
      }
    },

    (get & pathPrefix("assets" / Remaining)){ resourcePath =>
      getFromResource(s"assets/$resourcePath")
    },

    get(path("")(complete(HttpEntity(utf8, com.seanmcapp.html.index().body))))

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
    new Scheduler(airVisualService, "0 0 8 * * ?"),
    new Scheduler(airVisualService, "0 0 17 * * ?"),
    new Scheduler(nCovService, "0 0 20 * * ?"),
    new Scheduler(dsdaJakartaService, "0 0 0 * * ?"),
    new Scheduler(amarthaService, "0 0 18 * * ?"),
    new Scheduler(instagramService, "0 0 10 * * ?"),
    new Scheduler(instagramStoryService, "0 0 * * * ?"),
    new Scheduler(newsService, "0 0 6 * * ?"),
    new Scheduler(cacheCleanerService, "0 0 0 * * ?"),
  )

}
