package com.seanmcapp

import com.seanmcapp.client._
import com.seanmcapp.model.DashboardWallet
import com.seanmcapp.repository.{DatabaseClient, PeopleRepo, PeopleRepoImpl, WalletRepo, WalletRepoImpl}
import com.seanmcapp.service.{BirthdayService, NewsService, TelegramWebhookService, WalletService, WarmupDBService}
import com.seanmcapp.util.{JwtUtil, Scheduler}
import io.circe.syntax._
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import org.apache.pekko.http.scaladsl.server
import org.apache.pekko.http.scaladsl.server.{Directive0, Directives, Route}

import java.nio.file.{Files, Paths}
import scala.concurrent.ExecutionContext

// $COVERAGE-OFF$
class Bootstrap(implicit system: ActorSystem, ec: ExecutionContext) extends Directives {

  private val databaseClient: DatabaseClient = new DatabaseClient
  private val httpClient: HttpRequestClient = HttpRequestClientImpl
  private val telegramClient = new TelegramClient(httpClient)

  private val peopleRepo: PeopleRepo = new PeopleRepoImpl(databaseClient)
  private val walletRepo: WalletRepo = new WalletRepoImpl(databaseClient)

  private val walletService: WalletService = new WalletService(walletRepo)
  private val birthdayService = new BirthdayService(peopleRepo, telegramClient)
  private val newsService = new NewsService(httpClient, telegramClient)
  private val warmupDBService = new WarmupDBService(peopleRepo)
  private val telegramWebhookService = new TelegramWebhookService(telegramClient)

  private val authorizationString = "Authorization"
  private val frontEndPath = Paths.get("ui/.build")

  private val indexHTML = HttpEntity(ContentTypes.`text/html(UTF-8)`, Files.readString(frontEndPath.resolve("index.html")))
  private val frontEndRoute = {
    get(pathSingleSlash {
      complete(indexHTML)
    })
  } ~ pathPrefix("static") {
    getFromDirectory(frontEndPath.resolve("static").toString)
  } ~ get {
    complete(indexHTML)
  }

  private val route: server.Route =
    pathPrefix("api") {
      post {
        (path("webhook") & entity(as[String])) { payload =>
          val telegramUpdate = decode[TelegramUpdate](payload)
          complete(telegramWebhookService.receive(telegramUpdate).map(_.map(_.asJson.encode)))
        } ~ {
          pathPrefix("wallet") {
            authenticate {
              {
                (pathPrefix("create") & entity(as[String])) { payload =>
                  val dashboardWallet = decode[DashboardWallet](payload)
                  complete(walletService.create(dashboardWallet).map(_.toString))
                }
              } ~ {
                (pathPrefix("update") & entity(as[String])) { payload =>
                  val dashboardWallet = decode[DashboardWallet](payload)
                  complete(walletService.update(dashboardWallet).map(_.toString))
                }
              }
            }
          }
        }
      } ~ {
        pathPrefix("wallet") {
          get {
            {
              pathPrefix("login" / Segment) { password =>
                val token = JwtUtil.createToken(password)
                token match {
                  case Some(s) => complete(s)
                  case None => complete(StatusCodes.Unauthorized, "Invalid password")
                }
              }
            } ~ {
              authenticate {
                pathPrefix("dashboard") {
                  parameter("date".as[Int]) { date =>
                    complete(walletService.dashboard(date).map(_.asJson.encode))
                  }
                } ~ {
                  pathPrefix("delete" / Segment) { id =>
                    complete(walletService.delete(id.toInt).map(_.toString))
                  }
                }
              }
            }
          }
        }
      }
    } ~ frontEndRoute

  private def authenticate: Directive0 =
    headerValueByName(authorizationString).flatMap { token =>
      JwtUtil.validateToken(token) match {
        case Some(_) => pass
        case None    => complete(StatusCodes.Unauthorized, "Invalid token")
      }
    }

  private val scheduleList: List[Scheduler] = List(
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

  def init(): (Route, List[Scheduler]) = (route, scheduleList)

}
