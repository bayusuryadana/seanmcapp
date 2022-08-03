package com.seanmcapp

import io.circe.{Decoder, Json, Printer}
import io.circe.generic.AutoDerivation
import io.circe.parser

// $COVERAGE-OFF$
package object external extends AutoDerivation {

  implicit def decode[T: Decoder](input: String): T = {
    parser.decode[T](input) match {
      case Right(res) => res
      case Left(e) => throw new Exception(s"Unable to deserialize json response\n===== Exception =====\n$e\n\n===== INPUT =====\n$input")
    }
  }

  implicit class Encoder(json: Json) {
    def encode: String = json.printWith(Printer.noSpacesSortKeys)
  }
}
