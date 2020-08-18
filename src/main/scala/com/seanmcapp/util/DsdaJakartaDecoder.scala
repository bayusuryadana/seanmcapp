package com.seanmcapp.util

import scala.xml._

case class DsdaWaterGate(name: String, status: String)

case class DsdaWaterGateResponse(waterGates: Seq[DsdaWaterGate])

trait DsdaJakartaDecoder {

  def decode(node: Node): DsdaWaterGateResponse = {
    val waterGateNodes = (node \ "SP_GET_LAST_STATUS_PINTU_AIR").map{ w =>
      val name  = (w \ "NAMA_PINTU_AIR").text
      val status  = (w \ "STATUS_SIAGA").text
      DsdaWaterGate(name, status)
    }

    DsdaWaterGateResponse(waterGateNodes)
  }

}
