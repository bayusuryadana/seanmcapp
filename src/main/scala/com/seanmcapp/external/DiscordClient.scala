package com.seanmcapp.external

import ackcord._
import ackcord.commands.{CommandController, NamedCommand}
import ackcord.data.{OutgoingEmbed, OutgoingEmbedImage}
import ackcord.syntax._
import akka.NotUsed
import com.seanmcapp.DiscordConf
import com.seanmcapp.service.CBCService

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

class DiscordClient(cbcService: CBCService) {

  private val discordConf = DiscordConf()

  def run(): ackcord.DiscordClient = {
    val clientSettings = ClientSettings(discordConf.token)
    val client = Await.result(clientSettings.createClient(), Duration.Inf)
    client.commands.runNewNamedCommand(new DiscordController(client.requests, cbcService).command)
    client.login()
    client
  }
}

class DiscordController(requests: Requests, cbcService: CBCService) extends CommandController(requests) {
  val command: NamedCommand[NotUsed] = Command
    .named(Seq("!seanmcbot"), Seq("cbc", "recommendation", "help"))
    .asyncOptRequest { m =>
      val command = m.message.content.split(" ")(1)
      val resF = command match {
        case "help" =>
          val message = s"""```
                           |Perintah:
                           |cbc - gacha 1 foto
                           |recommendation - dengan bantuan AI, anda akan mendapatkan foto yg mirip dengan gacha terakhir (termasuk perintah ini)
                           |```
                           |""".stripMargin
          Future.successful(Some(m.textChannel.sendMessage(message)))
        case _ => cbcService.cbcFlow(m.user.id.toString.toLong, m.user.username, command).map(_.map{ photo =>
          val photoUrl = cbcService.getPhotoUrl(photo.id)
          m.textChannel.sendMessage(
            content = s"${photo.caption}\n@${photo.account}",
            embed = Some(OutgoingEmbed(
              image = Some(OutgoingEmbedImage(photoUrl))
            ))
          )
        })
      }

      OptFuture(resF)
    }
}
