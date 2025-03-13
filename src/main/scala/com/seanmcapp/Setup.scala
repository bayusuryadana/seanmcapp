package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import com.seanmcapp.external._
import com.seanmcapp.repository.{DatabaseClient, PeopleRepo, PeopleRepoImpl, WalletRepo, WalletRepoImpl}
import com.seanmcapp.service.{BirthdayService, NewsService, TelegramWebhookService, WarmupDBService}
import com.seanmcapp.util.ChatIdTypes
import io.circe.syntax._

import scala.concurrent.ExecutionContext
import scala.util.Try

// $COVERAGE-OFF$
class Setup(implicit system: ActorSystem, ec: ExecutionContext) extends Directives {

  private val databaseClient: DatabaseClient = new DatabaseClient
  private val httpClient: HttpRequestClient = HttpRequestClientImpl
  private val telegramClient = new TelegramClient(httpClient)

  private val peopleRepo: PeopleRepo = new PeopleRepoImpl(databaseClient)
  private val walletRepo: WalletRepo = new WalletRepoImpl(databaseClient)

  private val birthdayService = new BirthdayService(peopleRepo, telegramClient)
  private val newsService = new NewsService(httpClient, telegramClient)
  private val warmupDBService = new WarmupDBService(peopleRepo)
  private val telegramWebhookService = new TelegramWebhookService(telegramClient)

//  private val utf8 = ContentTypes.`text/html(UTF-8)`

  val route: server.Route =
    pathPrefix("api") {
      post {
        (path("webhook") & entity(as[String])) { payload =>
          val telegramUpdate = decode[TelegramUpdate](payload)
          complete(telegramWebhookService.receive(telegramUpdate).map(_.map(_.asJson.encode)))
        } ~
          (pathPrefix("login") & entity(as[String]) ) { payload =>
            complete(payload)
          } ~
          (pathPrefix("create") & entity(as[String]) ) { payload =>
            complete(payload)
          } ~
          (pathPrefix("update") & entity(as[String]) ) { payload =>
            complete(payload)
          } ~
          (pathPrefix("delete") & entity(as[String]) ) { payload =>
            complete(payload)
          }
      } ~
        get {
          (pathPrefix("dashboard") & entity(as[String]) ) { payload =>
            complete(payload)
          }
        }
    } ~
      get(path("")(complete("Konnichiwa sobat damemek !!!"))) // will serve UI here

  val scheduleList: List[Scheduler] = List(
    /**
      * this is not using normal cron convention format.
      * This cron4s expressions go from seconds to day of week in the following order:
      * (Seconds, Minutes, Hour Of Day, Day Of Month, Month, Day Of Week)
      */
    // warmup
    new Scheduler(warmupDBService, "0 * * * * ?", false),
    new Scheduler(warmupDBService, "*/5 * * * * ?", false),
    
    // real-time service
    new Scheduler(birthdayService, "0 0 6 * * ?"),
    new Scheduler(newsService, "0 0 8 * * ?"),
  )

}
