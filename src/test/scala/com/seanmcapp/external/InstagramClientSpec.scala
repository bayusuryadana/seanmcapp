package com.seanmcapp.external

import com.seanmcapp.service.{InstagramAccountResponse, InstagramCaption, InstagramData, InstagramEdge, InstagramEdgeCaption, InstagramMedia, InstagramMediaCaption, InstagramNode, InstagramPageInfo, InstagramResponse, InstagramUser}
import org.mockito.Mockito
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class InstagramClientSpec extends AnyWordSpec with Matchers {

  "getAccountResponse" in {
    val accountId = "262582140"
    val http = Mockito.mock(classOf[HttpRequestClient])
    val fetchResponse = Source.fromResource("instagram/init_response.json").mkString
    when(http.sendGetRequest(any())).thenReturn(fetchResponse)
    val instagramClient = new InstagramClient(http)
    val response = instagramClient.getAccountResponse(accountId)
    val expected = InstagramAccountResponse("profilePage_262582140")
    response shouldBe expected
  }

  "getPhotos" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val fetchResponse = Source.fromResource("instagram/fetch_response.json").mkString
    when(http.sendRequest(any(), any(), any(), any(), any())).thenReturn(fetchResponse)
    val instagramClient = new InstagramClient(http)
    val response = instagramClient.getPhotos("1", None, "")
    val expected = InstagramResponse(
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
    response shouldBe expected
  }
}
