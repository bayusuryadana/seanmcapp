package com.seanmcapp.scheduler

import java.net.URLEncoder
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Cancellable}
import akka.stream.Materializer
import com.seanmcapp.config.{AmarthaConf, SchedulerConf}
import com.seanmcapp.repository.birthday.PeopleRepoImpl
import com.seanmcapp.repository.dota.{Player, PlayerRepoImpl}
import com.seanmcapp.util.cache.MemoryCache
import com.seanmcapp.util.parser.{AirvisualData, AirvisualResponse, AmarthaAuthData, AmarthaMarketplaceData, AmarthaMarketplaceItem, AmarthaResponse, IgrowData, IgrowResponse, MatchResponse, PeerResponse, PlayerResponse}
import com.seanmcapp.util.requestbuilder.TelegramRequestBuilder
import org.joda.time.{DateTime, DateTimeZone, LocalDateTime}
import scalacache.memoization.memoizeSync
import scalacache.modes.sync._
import scalaj.http.Http
import spray.json._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}

abstract class Scheduler(startTime: Int, intervalOpt: Option[FiniteDuration])
                        (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext) extends TelegramRequestBuilder {

  private val ICT = "+07:00"
  protected def now: DateTime = new DateTime().toDateTime(DateTimeZone.forID(ICT))
  protected val scheduler = system.scheduler

  def run: Cancellable = {
    val startTimeDuration = getStartTimeDuration(startTime)
    intervalOpt match {
      case Some(interval) =>
        scheduler.schedule(startTimeDuration, interval)(task)
      case None => scheduler.scheduleOnce(startTimeDuration)(task)
    }
  }

  protected def getStartTimeDuration(hour: Int): FiniteDuration = {
    val init = new LocalDateTime()
      .withTime(hour,0,0,0)
      .toDateTime(DateTimeZone.forID(ICT))
    val target = if (now.getHourOfDay >= hour) init.plusDays(1) else init
    val numberInMillis = target.getMillis - now.getMillis
    Duration(numberInMillis, TimeUnit.MILLISECONDS)
  }

  protected def task: Any

}

class WarmupDBScheduler(startTime: Int)(implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, None) {

  private val peopleRepo = PeopleRepoImpl

  override def getStartTimeDuration(hour: Int): FiniteDuration = Duration(startTime, TimeUnit.SECONDS)

  override def task: Unit = {
    println("=== warmup database ===")
    val res = Await.result(peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear), Duration.Inf)
    println("warmup result: " + res)
  }

}

class BirthdayScheduler(startTime: Int, interval: FiniteDuration)
                       (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val peopleRepo = PeopleRepoImpl

  override def task: Future[String] = {
    println("=== birthday check ===")
    for{
      people <- peopleRepo.get(now.getDayOfMonth, now.getMonthOfYear)
    } yield {
      val result = "Today's birthday: " + people.map(_.name + ",")
      people.map { person =>
        sendMessage(274852283, "Today is " + person.name + "'s birthday !!")
        sendMessage(143635997, "Today is " + person.name + "'s birthday !!")
      }
      result
    }
  }

}

class IGrowScheduler(startTime: Int, interval: FiniteDuration)
                    (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val iGrowBaseUrl = "https://igrow.asia/api/public/en/v1/sponsor/seed"

  override def task: Seq[IgrowData] = {
    println("=== iGrow check ===")
    import com.seanmcapp.util.parser.IgrowJson._
    val response = Http(iGrowBaseUrl + "/list").asString.body.parseJson.convertTo[IgrowResponse].data.filter(_.stock > 0)
    val stringMessage = response.foldLeft("iGrow: %0A") { (res, data) =>
      res + "ada stok " + data.name + " sisa " + data.stock + " unit%0A"
    }
    val schedulerConf = SchedulerConf()
    schedulerConf.igrow.foreach(chatId => sendMessage(chatId, stringMessage))
    response
  }

}

class AirVisualScheduler(startTime: Int, interval: FiniteDuration)
                    (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val airVisualBaseUrl = "https://api.airvisual.com/v2/city"

  override def task: Unit = {
    println("=== AirVisual check ===")

    val cities = List(
      List("Indonesia", "Jakarta", "Jakarta"),
      List("Indonesia", "West Java", "Bekasi"),
      List("Indonesia", "West Java", "Depok"),
      List("Singapore", "Singapore", "Singapore")
    )

    val cityResults = cities.map(row => getCityResult(row:_*))

    val stringMessage = cityResults.foldLeft("*Seanmcearth* melaporkan kondisi udara saat ini:\n") { (res, data) =>
      res + data
    }

    sendMessage(-111546505, URLEncoder.encode(stringMessage, "UTF-8"))
    println("success")
  }

  private def getCityResult(city: String*): String = {
    import com.seanmcapp.util.parser.AirvisualJson._

    val encodedCity = city.map(c => URLEncoder.encode(c, "UTF-8"))

    val apiParams = "?country=%s&state=%s&city=%s&key=1c9492cc-4e66-488b-854f-d691db38406a"
    val apiUrl = airVisualBaseUrl + apiParams.format(encodedCity(0), encodedCity(1), encodedCity(2))

    val response = Http(apiUrl).asString.body.parseJson.convertTo[AirvisualResponse].data
    val aqius = response.current.pollution.aqius

    "\n" + city(2) + " (AQI " + aqius + " " + getEmojiFromAqi(aqius) + ")"
  }

  private def getEmojiFromAqi(aqi: Int): String = {
    aqi match {
      case aqi if aqi <= 50 => new String(Array(0x1F340), 0, Array(0x1F340).length)
      case aqi if aqi > 50 & aqi <= 100 => new String(Array(0x1F60E), 0, Array(0x1F60E).length)
      case aqi if aqi > 100 & aqi <= 150 => new String(Array(0x1F630), 0, Array(0x1F630).length)
      case aqi if aqi > 150 & aqi <= 200 => new String(Array(0x1F637), 0, Array(0x1F637).length)
      case aqi if aqi > 200 => new String(Array(0x1F480), 0, Array(0x1F480).length)
    }
  }

}

class AmarthaScheduler(startTime: Int, interval: FiniteDuration)
                      (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) with MemoryCache {

  import com.seanmcapp.util.parser.AmarthaJson._

  private val amarthaBaseUrl = "https://dashboard.amartha.com/v2"
  private val duration = Duration(15, TimeUnit.MINUTES)
  implicit val amarthaCache = createCache[AmarthaResponse]

  override def task: Seq[AmarthaMarketplaceItem] = {
    println(" === amartha check ===")
    val authResponse: AmarthaResponse = memoizeSync(Some(duration))(auth)
    if (authResponse.code == 200) {
      val authData = authResponse.data.convertTo[AmarthaAuthData]
      println("account: " + authData.name)
      val response = Http(amarthaBaseUrl + "/marketplace")
        .header("x-access-token", authData.accessToken)
        .timeout(15000, 300000)
        .asString.body.parseJson.convertTo[AmarthaResponse].data.convertTo[AmarthaMarketplaceData]
      println(response.marketplace)

      val stringMessage = "Amartha: " + response.marketplace.size + " orang perlu didanai " + "(" + startTime + ":00)"
      val schedulerConf = SchedulerConf()
      schedulerConf.amartha.foreach(chatId => sendMessage(chatId, stringMessage))
      response.marketplace
    } else throw new Exception(authResponse.toString)
  }

  private def auth: AmarthaResponse = {
    val amarthaConf = AmarthaConf()
    Http(amarthaBaseUrl + "/auth")
      .postData(s"""{"username": "${amarthaConf.username}","password": "${amarthaConf.password}"}""")
      .header("Content-Type", "application/json")
      .timeout(15000, 300000)
      .asString.body.parseJson.convertTo[AmarthaResponse]
  }

}

class DotaMetadataFetcherScheduler(startTime: Int, interval: FiniteDuration)
                                  (implicit system: ActorSystem, mat: Materializer, ec: ExecutionContext)
  extends Scheduler(startTime, Some(interval)) {

  private val playerRepo = PlayerRepoImpl
  private val dotaBaseUrl = "https://api.opendota.com/api/players/"

  override def task: Future[Seq[PlayerResponse]] = {
    println("=== dota metadata fetching ===")
    import com.seanmcapp.util.parser.DotaInputJson._
    for {
      players <- playerRepo.getAll
    } yield {
      players.map { player =>
        val playerResult = Http(dotaBaseUrl + player.id).asString.body.parseJson.convertTo[PlayerResponse]
        val playerModel = Player(player.id, player.realName,
          playerResult.profile.avatarfull, playerResult.profile.personaName, playerResult.rankTier)
        playerRepo.update(playerModel)
        playerResult
      }
    }
  }

}


