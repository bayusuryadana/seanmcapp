package com.seanmcapp.util.parser.decoder

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.unmarshalling._

import scala.xml._

case class DsdaWaterGate(name: String, status: String)

case class DsdaWaterGateResponse(waterGates: Seq[DsdaWaterGate])
object DsdaWaterGateResponse {
  def fromXml(node: Node): Seq[DsdaWaterGate]  = {
    val waterGateNodes = (node \ "SP_GET_LAST_STATUS_PINTU_AIR")
    waterGateNodes.map{ w =>
      val name  = (w \ "NAMA_PINTU_AIR").text
      val status  = (w \ "STATUS_SIAGA").text
      DsdaWaterGate(name, status)
    }
  }
}

trait DsdaJakartaDecoder {
  def decode(node: Node): DsdaWaterGateResponse = {
    DsdaWaterGateResponse(DsdaWaterGateResponse.fromXml(node))
  }
}
