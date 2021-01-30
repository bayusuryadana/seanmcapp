package com.seanmcapp.external

import ackcord._
import ackcord.commands.{CommandController, NamedCommand, UserCommandMessage}
import ackcord.data.{OutgoingEmbed, OutgoingEmbedImage}
import ackcord.requests.CreateMessage
import ackcord.syntax._
import akka.NotUsed
import com.seanmcapp.DiscordConf
import com.seanmcapp.service.{CBCService, HadithService}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

// $COVERAGE-OFF$
class DiscordClient(cbcService: CBCService, hadithService: HadithService) {

  private val discordConf = DiscordConf()

  def run(): ackcord.DiscordClient = {
    val clientSettings = ClientSettings(discordConf.token)
    val client = Await.result(clientSettings.createClient(), Duration.Inf)
    client.commands.runNewNamedCommand(new DiscordController(client.requests, cbcService, hadithService).command)
    client.login()
    client
  }
}

class DiscordController(requests: Requests, cbcService: CBCService, hadithService: HadithService) extends CommandController(requests) {

  val command: NamedCommand[NotUsed] = Command.named(Seq("!cbc", "!hadith"), Seq("g", "r", "help"))
    .asyncOptRequest { m =>
      val commandStringList = m.message.content.split(" ")
      val service = commandStringList(0)
      val resF = service match {
        case "!cbc" => cbcFlow(m, commandStringList(1))
        case "!hadith" => hadithFlow(m)
      }

      OptFuture(resF)
    }

  private def cbcFlow(m: UserCommandMessage[NotUsed], command: String): Future[Option[CreateMessage]] = {
    command match {
      case "help" =>
        val message =
          s"""```
             |Perintah:
             |g - gacha
             |r - recommend
             |```
             |""".stripMargin
        Future.successful(Some(m.textChannel.sendMessage(message)))
      case _ =>
        val `type` = command match {
          case "g" => "cbc"
          case "r" => "recommendation"
          case _ => throw new Exception("command not recognized")
        }
        cbcService.cbcFlow(m.user.id.toString.toLong, m.user.username, `type`).map(_.map { photo =>
          val photoUrl = cbcService.getPhotoUrl(photo.id)
          m.textChannel.sendMessage(
            content = s"${photo.caption}\n@${photo.account}",
            embed = Some(OutgoingEmbed(
              image = Some(OutgoingEmbedImage(photoUrl))
            ))
          )
        })
    }
  }

  private def hadithFlow(m: UserCommandMessage[NotUsed]): Future[Option[CreateMessage]] = {
    val hadith = hadithService.random
    Future.successful(Some(m.textChannel.sendMessage(hadith)))
  }
}
