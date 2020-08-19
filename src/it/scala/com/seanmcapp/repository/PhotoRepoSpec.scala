package com.seanmcapp.repository

import com.seanmcapp.repository.instagram.{Photo, PhotoRepoImpl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class PhotoRepoSpec extends AsyncWordSpec with Matchers {

  "should return all photo" in {
    val response = PhotoRepoImpl.getAll
    response.map { res =>
      res.size shouldEqual 15
      res.headOption.map(_.caption) shouldEqual Some("Raihanah Yasmin. Vokasi'12")
    }
  }

  "should return a photo" in {
    val response = PhotoRepoImpl.get(775233039855010642L)
    response.map { res =>
      res.map(_.caption) shouldEqual Some("Raihanah Yasmin. Vokasi'12")
    }
  }

  "should return random photo" in {
    val response = PhotoRepoImpl.getRandom(None)
    response.map { res =>
      res should not be None
    }
  }

  "should return not return photo from particular account" in {
    val response = PhotoRepoImpl.getRandom(Some("ugmcantik"))
    response.map { res =>
      res shouldBe None
    }
  }

  "should return photo from particular account" in {
    val response = PhotoRepoImpl.getRandom(Some("ui.cantik"))
    response.map { res =>
      res should not be None
      res.map(_.account) shouldEqual Some("ui.cantik")
    }
  }

  "insert function should properly inserted data into DB" in {
    val photos = Seq(
      Photo(123L, "https://someurl", 1406252001, "Vera Verdiana. Psikologi'12", "ui.cantik"),
      Photo(456L, "https://someurl", 1432308647, "Tiara Anugrah. Hukum 2014", "ugmcantik"),
    )
    val response = PhotoRepoImpl.insert(photos)
    response.map { res =>
      PhotoRepoImpl.delete(photos.map(_.id))
      res shouldBe Some(2)
    }
  }

}
