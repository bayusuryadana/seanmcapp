package com.seanmcapp.service

import com.seanmcapp.repository.instagram.{Account, AccountGroupTypes, AccountRepo, Photo}
import com.seanmcapp.repository.{CustomerRepoMock, FileRepo, PhotoRepoMock}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{mock, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future
import scala.io.Source

class CBCServiceSpec extends AsyncWordSpec with Matchers {

  import com.seanmcapp.external._

  val cbcClient = Mockito.mock(classOf[CBCClient])
  val responseMock = Source.fromResource("instagram/knn.csv").getLines().map { line =>
    val items = line.split(",")
    items.head.toLong -> items.tail.map(_.toLong)
  }.toMap
  when(cbcClient.getRecommendation).thenReturn(responseMock)
  val fileRepoMock = mock(classOf[FileRepo])
  val accountRepoMock = mock(classOf[AccountRepo])
  val instagramClient = mock(classOf[InstagramClient])
  val cbcService = new CBCService(PhotoRepoMock, CustomerRepoMock, fileRepoMock, accountRepoMock, cbcClient, instagramClient) {
    override val regexMapping = Map("ugmcantik" -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r)
    override def savingToStorage(filteredPhotos: Seq[Photo]): Seq[Photo] = filteredPhotos
  }

  val userId = 274852283
  val userFullName = "Yukihira Soma"

  "should return a random photos - cbc" in {
    cbcService.cbcFlow(userId, userFullName, "cbc").map { response =>
      response shouldNot be(None)
    }
  }

  "should return a recommendation photos based on recommendation csv file - recommendation" in {
    // this test will be based on the last fetched photo in previous test above, please keep in mind
    cbcService.cbcFlow(userId, userFullName, "recommendation").map { response =>
      response shouldNot be(None)
      val res = response.getOrElse(cancel("response is not defined"))
      res.id shouldEqual 884893623514815734L
      res.caption shouldEqual "Delicia Gemma. Hukum 2011"
      res.account shouldEqual "unpad.geulis"
    }
  }

  "should throw an exception if command is not valid" in {
    assertThrows[Exception] {
      cbcService.cbcFlow(1, "name", "wow")
    }
  }

  "should return number of image that have been successfully fetched" in {
    //when(instagramClient.getAccountResponse(any())).thenReturn(InstagramAccountResponse("profilePage_262582140"))
    when(instagramClient.postLogin()).thenReturn("")
    when(accountRepoMock.getAll(any())).thenReturn(Future.successful(Seq(Account("262582140", "ugmcantik", AccountGroupTypes.CBC))))
    val photoMock = List(
      InstagramNode(
        "2115041543081728221",
        "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
        1566352621,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Hasna Izdihar. Magister Kenotariatan 2019\n#ugmcantik")))),
        "https://scontent-sin2-1.cdninstagram.com/vp/43571e6fb4e7d46752da94fc854a73f6/5E13F043/t51.2885-15/fr/e15/s1080x1080/67447213_218558379121900_7578318177720267803_n.jpg?_nc_ht=scontent-sin2-1.cdninstagram.com",
        false,
        None,
        None
      ),
      InstagramNode(
        "2114593284248831645",
        "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
        1566299184,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Just a caption")))),
        "https://scontent-sin2-1.cdninstagram.com/vp/b87609220db141e7db124b3c4f77185e/5DF5DF11/t51.2885-15/fr/e15/p1080x1080/66661723_494746641100189_7024627927498649957_n.jpg?_nc_ht=scontent-sin2-1.cdninstagram.com",
        false,
        None,
        None
      ),
      InstagramNode(
        "2114394029768926432",
        "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
        1566275431,
        InstagramEdgeMediaCaption(List(InstagramNodeCaption(InstagramCaption("Yuan Shafira. FT 2019")))),
        "https://scontent-sin2-1.cdninstagram.com/vp/e81262628c99ba6a42596f2f81e36e2c/5E0A0E88/t51.2885-15/e15/66660256_2936655829742039_2990571628404942465_n.jpg?_nc_ht=scontent-sin2-1.cdninstagram.com",
        false,
        None,
        None
      )
    )
    when(instagramClient.getAllPosts(any(), any(), any(), any())).thenReturn(photoMock)

    cbcService.run().map { res =>
      res shouldBe Seq(Some(1))
    }
  }
}
