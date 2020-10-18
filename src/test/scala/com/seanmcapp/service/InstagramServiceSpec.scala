package com.seanmcapp.service

import com.seanmcapp.external.{HttpRequestClient, InstagramClient}
import com.seanmcapp.repository.{FileRepo, PhotoRepoMock}
import com.seanmcapp.repository.instagram.Photo
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.io.Source

class InstagramServiceSpec extends AsyncWordSpec with Matchers {

  "should return number of image that have been successfully fetched" in {

    val fileRepoMock = mock(classOf[FileRepo])

    val http = mock(classOf[HttpRequestClient])
    val instagramClient = mock(classOf[InstagramClient])

    val accountName = "ugmcantik"
    val initUrl = "https://www.instagram.com/" + accountName + "/?__a=1"
    val initResponse = Source.fromResource("instagram/init_response.json").mkString
    when(http.sendGetRequest(ArgumentMatchers.eq(initUrl), any())).thenReturn(initResponse)
    when(instagramClient.getAccountResponse(any())).thenReturn(InstagramAccountResponse("profilePage_262582140"))
    val photoMock = InstagramResponse(
      InstagramData(
        InstagramUser(
          InstagramMedia(1194, InstagramPageInfo(false, None),
            List(
              InstagramEdge(InstagramNode(
                "2115041543081728221",
                "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
                1566352621,
                InstagramMediaCaption(List(InstagramEdgeCaption(InstagramCaption("Hasna Izdihar. Magister Kenotariatan 2019\n#ugmcantik"))))
              )),
              InstagramEdge(InstagramNode(
                "2114593284248831645",
                "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
                1566299184,
                InstagramMediaCaption(List(InstagramEdgeCaption(InstagramCaption("Just a caption"))))
              )),
              InstagramEdge(InstagramNode(
                "2114394029768926432",
                "https://instagram.fsin9-1.fna.fbcdn.net/v/t51.2885-15/e35/p1080x1080/82869766_181890369788913_4934734302708118273_n.jpg?_nc_ht=instagram.fsin9-1.fna.fbcdn.net&_nc_cat=1&_nc_ohc=LgwayFJRmJQAX-zZgbo&oh=589b1d4c0cb9d4845a227d77d9bdd41f&oe=5EDCB9DE",
                1566275431,
                InstagramMediaCaption(List(InstagramEdgeCaption(InstagramCaption("Yuan Shafira. FT 2019"))))
              ))
            )
          )
        )
      )
    )
    when(instagramClient.getPhotos(any(), any(), any())).thenReturn(photoMock)

    val instagramService = new InstagramService(PhotoRepoMock, fileRepoMock, instagramClient) {
      override val accountList = Map(accountName -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r)
      override def savingToStorage(filteredPhotos: Seq[Photo]): Seq[Photo] = filteredPhotos
    }

    instagramService.run().map { res =>
      res shouldBe Seq(Some(1))
    }
  }

}
