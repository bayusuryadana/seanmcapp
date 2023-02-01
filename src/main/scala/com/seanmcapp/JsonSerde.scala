package com.seanmcapp


import com.seanmcapp.util.ExceptionHandler
import io.circe.{Decoder, Json, Printer, parser}
import io.circe.generic.AutoDerivation

// $COVERAGE-OFF$
object JsonSerde {
  import io.circe._, io.circe.generic.semiauto._
  import com.seanmcapp.repository.seanmcmamen.City
  import com.seanmcapp.repository.seanmcmamen.Diner
  implicit val citiesDecoder: Decoder[City] = deriveDecoder
  implicit val citiesEncoder: Encoder[City] = deriveEncoder
  implicit val dinerDecoder: Decoder[Diner] = deriveDecoder
  implicit val dinerEncoder: Encoder[Diner] = deriveEncoder
}

package object external extends AutoDerivation {

  implicit def decode[T: Decoder](input: String): T = {
    parser.decode[T](input) match {
      case Right(res) => res
      case Left(e) =>
        println(s"[ERROR] parse failed. ${e.getMessage}")
        throw new SerdeException(input, e)
    }
  }

  implicit class Encoder(json: Json) {
    def encode: String = json.printWith(Printer.noSpacesSortKeys)
  }
  
  class SerdeException(input: String, t: Throwable) extends ExceptionHandler(t) {
    override val processedMessage: String = {
      s"""${this.getMessage}
         |input: $input
         |""".stripMargin
    }
  }
}
