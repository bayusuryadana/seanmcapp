package com.seanmcapp.external

import com.seanmcapp.service.{InstagramAccountResponse, InstagramCaption, InstagramData, InstagramEdge, InstagramEdgeCaption, InstagramMedia, InstagramMediaCaption, InstagramNode, InstagramPageInfo, InstagramResponse, InstagramStoryData, InstagramStoryItem, InstagramStoryReel, InstagramStoryResponse, InstagramStoryVideoResource, InstagramUser}
import org.mockito.Mockito
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import scalaj.http.HttpResponse

import scala.io.Source

class InstagramClientSpec extends AnyWordSpec with Matchers {

  val http = Mockito.mock(classOf[HttpRequestClient])
  val instagramClient = new InstagramClient(http)

  "postLogin" in {
    when(http.sendGetRequest(any(), any())).thenReturn("\"csrf_token\":\"token\"")
    val httpResponse = HttpResponse[String]("", 200, Map("Set-Cookie" -> Vector("sessionid=session;")))
    when(http.sendRequest(any(), any(), any(), any(), any(), any())).thenReturn(httpResponse)
    val response = instagramClient.postLogin()
    response shouldEqual "session"
  }

  "getAccountResponse" in {
    val accountId = "262582140"
    val fetchResponse = Source.fromResource("instagram/init_response.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(fetchResponse)
    val response = instagramClient.getAccountResponse(accountId)
    val expected = InstagramAccountResponse("profilePage_262582140")
    response shouldBe expected
  }

  "getPhotos" in {
    val fetchResponse = Source.fromResource("instagram/fetch_response.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(fetchResponse)
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

  "getStories" in {
    val fetchResponse = Source.fromResource("instagram/fetch_stories.json").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(fetchResponse)
    val response = instagramClient.getStories("1", "")
    val expected = InstagramStoryResponse(
      InstagramStoryData(List(
        InstagramStoryReel(List(
          InstagramStoryItem("2424716613985265804","GraphStoryImage","https://pic1.url",None),
          InstagramStoryItem("2424802368570123380","GraphStoryVideo","https://pic2.url",
            Some(List(
              InstagramStoryVideoResource("https://video-baseline.url","BASELINE"),
              InstagramStoryVideoResource("https://video-main.url","MAIN"))
            ))
        ))
      ))
    )
    response shouldBe expected
  }
}
