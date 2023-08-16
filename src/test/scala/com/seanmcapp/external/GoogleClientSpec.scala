package com.seanmcapp.external

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GoogleClientSpec extends AnyWordSpec with Matchers {

  "fetchLatLng" in {
    val http = Mockito.mock(classOf[HttpRequestClient])
    val googleClient = new GoogleClient(http)
    val response =
      s"""
         |{
         |   "results" : [
         |      {
         |         "address_components" : [
         |            {
         |               "long_name" : "M9WC+GJ",
         |               "short_name" : "M9WC+GJ",
         |               "types" : [ "plus_code" ]
         |            },
         |            {
         |               "long_name" : "Benteng Pasar Atas",
         |               "short_name" : "Benteng Pasar Atas",
         |               "types" : [ "administrative_area_level_4", "political" ]
         |            },
         |            {
         |               "long_name" : "Guguk Panjang",
         |               "short_name" : "Guguk Panjang",
         |               "types" : [ "administrative_area_level_3", "political" ]
         |            },
         |            {
         |               "long_name" : "Bukittinggi City",
         |               "short_name" : "Bukittinggi City",
         |               "types" : [ "administrative_area_level_2", "political" ]
         |            },
         |            {
         |               "long_name" : "West Sumatra",
         |               "short_name" : "West Sumatra",
         |               "types" : [ "administrative_area_level_1", "political" ]
         |            },
         |            {
         |               "long_name" : "Indonesia",
         |               "short_name" : "ID",
         |               "types" : [ "country", "political" ]
         |            }
         |         ],
         |         "formatted_address" : "M9WC+GJ, Benteng Pasar Atas, Guguk Panjang, Bukittinggi City, West Sumatra, Indonesia",
         |         "geometry" : {
         |            "location" : {
         |               "lat" : -0.3036875,
         |               "lng" : 100.3715625
         |            },
         |            "location_type" : "GEOMETRIC_CENTER",
         |            "viewport" : {
         |               "northeast" : {
         |                  "lat" : -0.302338519708498,
         |                  "lng" : 100.3729114802915
         |               },
         |               "southwest" : {
         |                  "lat" : -0.305036480291502,
         |                  "lng" : 100.3702135197085
         |               }
         |            }
         |         },
         |         "place_id" : "ElVNOVdDK0dKLCBCZW50ZW5nIFBhc2FyIEF0YXMsIEd1Z3VrIFBhbmphbmcsIEJ1a2l0dGluZ2dpIENpdHksIFdlc3QgU3VtYXRyYSwgSW5kb25lc2lhIiY6JAoKDTWp0f8VKXzTOxAKGhQKEgmZvt0bozjVLxECR9Uuay8ybQ",
         |         "plus_code" : {
         |            "compound_code" : "M9WC+GJ Benteng Pasar Atas, Bukittinggi City, West Sumatra, Indonesia",
         |            "global_code" : "6PF2M9WC+GJ"
         |         },
         |         "types" : [ "street_address" ]
         |      }
         |   ],
         |   "status" : "OK"
         |}
         |""".stripMargin
    when(http.sendGetRequest(any(), any())).thenReturn(response)
    val expected = (Some(-0.3036875),Some(100.3715625))
    googleClient.fetchLatLng("") shouldBe expected
  }

}
