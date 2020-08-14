package com.seanmcapp

import io.circe.Decoder
import io.circe.generic.AutoDerivation
import io.circe.parser.decode

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait JsonDecoder[T] {
  def apply(s: String): Either[String, T]
}

object DecoderProvider {
  def apply[T: Decoder]: JsonDecoder[T] =
    (s: String) => decode[T](s).left.map(_.getMessage)
}

package object external extends AutoDerivation {
  implicit def decoder[T: Decoder]: JsonDecoder[T] = DecoderProvider[T]

  implicit def decode[T: Decoder](responseF: Future[Either[String, String]]): Future[Either[String, T]] = {
    responseF.map(_.map(response => decoder[T].apply(response)).joinRight)
  }
}
