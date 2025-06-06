package com.seanmcapp


import com.seanmcapp.util.ExceptionHandler
import io.circe.{Decoder, Json, Printer, parser}
import io.circe.generic.AutoDerivation

import scala.language.implicitConversions

// $COVERAGE-OFF$
package object client extends AutoDerivation {

  implicit def decode[T: Decoder](input: String): T = {
    parser.decode[T](input) match {
      case Right(res) => res
      case Left(e) =>
        println(s"[ERROR] parse failed. ${e.getMessage}")
        throw new SerdeException(input, e)
    }
  }

  implicit class Encoder(json: Json) {
    def encode: String = json.printWith(Printer.spaces4)
  }
  
  private class SerdeException(input: String, t: Throwable) extends ExceptionHandler(t) {
    override val processedMessage: String = {
      s"""${this.getMessage}
         |input: $input
         |""".stripMargin
    }
  }
}
