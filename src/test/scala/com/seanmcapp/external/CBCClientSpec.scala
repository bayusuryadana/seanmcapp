package com.seanmcapp.external

import com.seanmcapp.StorageConf
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source

class CBCClientSpec extends AnyWordSpec with Matchers {

  "getRecommendation" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val cbcClient = new CBCClient(http) {
      override val storageConf = StorageConf("access", "secret", "host", "bucket")
    }
    val mockResponse = Source.fromResource("instagram/knn.csv").mkString
    when(http.sendGetRequest(any(), any())).thenReturn(mockResponse)
    val expected = Map(
      1699704484487729075L -> Array(2093920084464867448L, 1966150887791311229L, 2231065756832796103L, 1681917276393329115L, 1512865706260228584L),
      2197263767212894174L -> Array(1625747162149643196L, 1539532601709498694L, 1751421769561283743L, 1066637181476134902L, 1436167541432803036L),
      2241324772649595331L -> Array(2151188861963008771L, 2070848629770245291L, 2093617525577585271L, 1530262302003396319L, 2121297678553809153L),
      772020198343721705L -> Array(884893623514815734L),
      1413884082743596438L -> Array(1677568639571872459L, 1416437652152828413L, 1539667935944506019L, 1563494787422969320L, 1301463391021610216L),
      1116926637369974369L -> Array(1651839082307088358L, 964514621432906039L, 1150631624348236841L, 1468242167398781301L, 1876198208183829691L)
    )
    val result = cbcClient.getRecommendation
    result.keys shouldBe expected.keys
    result.values should contain theSameElementsAs expected.values
  }

}
