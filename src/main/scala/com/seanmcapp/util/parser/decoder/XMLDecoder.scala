package com.seanmcapp.util.parser.decoder

import scala.xml.Node

trait XMLDecoder[T] {

  def decode(node: Node): T

}
