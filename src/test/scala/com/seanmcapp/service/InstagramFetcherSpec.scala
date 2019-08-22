package com.seanmcapp.service

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import com.seanmcapp.mock.repository.PhotoRepoMock
import com.seanmcapp.storage.ImageStorage
import com.seanmcapp.util.requestbuilder.HttpRequestBuilder
import org.mockito.ArgumentMatchers
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.io.Source

class InstagramFetcherSpec extends AsyncWordSpec with Matchers {

  val imageStorageMock = mock(classOf[ImageStorage])
  when(imageStorageMock.put(any(), any())).thenReturn(Some())

  val http = mock(classOf[HttpRequestBuilder])
  val accountName = "ugmcantik"
  val initUrl = "https://www.instagram.com/" + accountName + "/?__a=1"
  val initResponse = Source.fromResource("instagram/init_response.json").mkString
  when(http.sendRequest(ArgumentMatchers.eq(initUrl), any(), any(), any())).thenReturn(initResponse)

  val defaultFetchUrl = "https://www.instagram.com/graphql/query/?query_id=17888483320059182&id=<user_id>&first=50&after="
  val fetchUrl = defaultFetchUrl.replace("<user_id>", "262582140")
  val fetchResponse = Source.fromResource("instagram/fetch_response.json").mkString
  when(http.sendRequest(ArgumentMatchers.eq(fetchUrl), any(), any(), any())).thenReturn(fetchResponse)

  val instagramFetcher = new InstagramFetcher(PhotoRepoMock, imageStorageMock, http) {
    override val accountList = Map(accountName -> "[\\w ]+\\. [\\w]+ \\d\\d\\d\\d".r)
  }

  "should return number of image that have been successfully fetched" in {
    instagramFetcher.fetch("").map { res =>
      res shouldBe Seq(Some(1))
    }
  }

}
