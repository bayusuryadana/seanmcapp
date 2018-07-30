package com.seanmcapp.fetcher

import com.seanmcapp.{DBGenerator, InjectionTest}
import org.scalatest.{AsyncWordSpec, Matchers}

class InstagramFetcherSpec extends AsyncWordSpec with Matchers with InjectionTest {

  DBGenerator.generate

  "sync call" in {
    instagramFetcher.flow.map { res =>
      res.toString should equal ("[[\"ui.cantik\",[{\"id\":\"1832730068440590249\",\"caption\":\"Pamela Sidarta. FTUI’18\",\"thumbnail_src\":\"https://instagram.fbkk8-2.fna.fbcdn.net/vp/37c66e4ee2fd8b7a4fde3e22a6ee8a3f/5C1436DD/t51.2885-15/sh0.08/e35/c0.135.1080.1080/s640x640/37895376_1902483769828463_1029784315489157120_n.jpg\",\"date\":1500000000}]]]")
    }
  }

}
