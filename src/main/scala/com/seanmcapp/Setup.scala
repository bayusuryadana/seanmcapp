package com.seanmcapp

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives
import com.seanmcapp.external._
import com.seanmcapp.util.ChatIdTypes
import io.circe.syntax._

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
//    get(path( "api" / "instagram" / "push" / Remaining) { session =>
//      val sessionOpt = if (session == "null") None else Some(session)
//      complete(cbcService.startFetching(sessionOpt).map(_.asJson.encode))
//    }),
//    get(path( "api" / "instagram" / "pull" / Remaining) { session =>
//      val sessionOpt = if (session == "null") None else Some(session)
//      val postsF = stalkerService.fetchPosts(AccountGroupTypes.Stalker, ChatIdTypes.Personal, sessionOpt)
//      val storiesF = stalkerService.fetchStories(AccountGroupTypes.Stalker, ChatIdTypes.Personal, sessionOpt)
//      val result = for {
//        posts <- postsF
//        stories <- storiesF
//      } yield {
//        posts ++ stories
//      }
//      complete(result.map(_.asJson.encode))
//    }),
//    get(path( "api" / "instagram" / "special" / Remaining) { session =>
//      val sessionOpt = if (session == "null") None else Some(session)
//      complete(stalkerService.fetchPosts(AccountGroupTypes.StalkerSpecial, ChatIdTypes.Personal, sessionOpt).map(_.asJson.encode))
//    }), 
//    get(path( "api" / "tweet" )(complete(twitterService.run.map(_.asJson.encode)))),
    post((path( "api" / "mamen") & entity(as[String])) { request =>
      complete(mamenService.search(decode[MamenRequest](request)).map(_.asJson.encode))
    }),
    get(path( "api" / "mamen" )(complete(mamenService.fetch().map(_.asJson.encode)))),
    get(path( "api" / "metadota" )(complete(dotaService.run.map(_.asJson.encode)))),
    get(path("api" / "news")(complete(newsService.process(ChatIdTypes.Personal).asJson.encode))),
    /////////// WEB ///////////
    get(path("dota")(complete(dotaService.home.map(HttpEntity(utf8, _))))),

    pathPrefix("wallet") {
      post {
        (pathPrefix("do_login") & formField(Symbol("secretKey"))) { secret =>
          if (walletService.login(secret)) {
            setSession(secret)(_.redirect("/wallet", StatusCodes.Found))
          } else {
            redirect("/wallet/login", StatusCodes.SeeOther)
          }
        } ~ validateSession { session => formFieldMap { fields =>
          pathPrefix( "data" / "create") {
            val date = walletService.create(session, fields)
            redirect(s"/wallet/data?date=$date", StatusCodes.SeeOther)
          } ~ pathPrefix( "data" / "update") {
            val date = walletService.update(session, fields)
            redirect(s"/wallet/data?date=$date", StatusCodes.SeeOther)
          } ~ pathPrefix("data" / "delete") { 
            val date = walletService.delete(session, fields)
            redirect(s"/wallet/data?date=$date", StatusCodes.SeeOther)
          }
        }}
      } ~
      get {
        pathPrefix("login") {
          complete(HttpEntity(utf8, com.seanmcapp.wallet.html.login().body))
        } ~
        validateSession { session =>
          pathEndOrSingleSlash {
            val dashboardView = walletService.dashboard(session)
            _.complete(HttpEntity(utf8, com.seanmcapp.wallet.html.dashboard(dashboardView).body))
          } ~ (pathPrefix("data") & parameters(Symbol("date").?)) { date =>
            val dataView = walletService.data(session, date.flatMap(d => Try(d.toInt).toOption))
            _.complete(HttpEntity(utf8, com.seanmcapp.wallet.html.data(dataView).body))
          } ~ pathPrefix("do_logout") {
            invalidateSession(_.redirect("/wallet/login", StatusCodes.Found))
          }
        }
      }
    },
    
    get(pathPrefix("mamen")(complete(HttpEntity(utf8, com.seanmcapp.mamen.html.home().body)))),

    (get & pathPrefix("assets" / Remaining)){ resourcePath =>
      getFromResource(s"assets/$resourcePath")
    },

    get(path("")(complete(HttpEntity(utf8, com.seanmcapp.html.index().body))))

  ).reduce{ (a,b) => a~b }

  val scheduleList: List[Scheduler] = List(
    /**
      * this is not using normal cron convention format.
      * This cron4s expressions go from seconds to day of week in the following order:
      * (Seconds, Minutes, Hour Of Day, Day Of Month, Month, Day Of Week)
      */
    // warmup
    new Scheduler(warmupDBService, "0 * * * * ?", false),
    new Scheduler(warmupDBService, "*/5 * * * * ?", false),
    
    //pre-loading data service
    new Scheduler(dotaService, "0 0 2 * * ?"),
//    new Scheduler(cbcService, "0 0 10 * * ?"),
    
    // real-time service
    new Scheduler(birthdayService, "0 0 6 * * ?"),
    new Scheduler(newsService, "0 0 8 * * ?"),
//    new Scheduler(twitterService, "0 0 * * * ?"),
//    new Scheduler(stalkerService, "0 0 * * * ?"),
//    new Scheduler(specialStalkerService, "0 20 * * * ?"),
//    new Scheduler(cacheCleanerService, "0 0 0 * * ?"),
  )

}
