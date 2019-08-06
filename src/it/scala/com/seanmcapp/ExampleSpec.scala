package com.seanmcapp

import org.scalatest.{AsyncWordSpec, Matchers}

class ExampleSpec extends AsyncWordSpec with Matchers {
  "true should be true" in {
    true shouldBe true
  }
}
