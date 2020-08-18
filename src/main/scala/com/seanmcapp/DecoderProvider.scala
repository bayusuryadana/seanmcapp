package com.seanmcapp

import io.circe.Decoder
import io.circe.generic.AutoDerivation
import io.circe.parser.decode

trait JsonDecoder[T] {
  def apply(s: String): Either[String, T]
}

object DecoderProvider {
  def apply[T: Decoder]: JsonDecoder[T] =
    (s: String) => decode[T](s).left.map(_.getMessage)
}

package object external extends AutoDerivation {
    implicit def decode[T: Decoder](input: String): T = {
      DecoderProvider[T].apply(input) match {
        case Right(res) => res
        case Left(e) => throw new Exception(s"Unable to deserialize json response\n$e\n:\n$input")
      }
    }
}
