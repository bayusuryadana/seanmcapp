package com.seanmcapp.service

import com.seanmcapp.external.{GeoFilter, LatLng, MamenFilter, MamenRequest}
import com.seanmcapp.repository.seanmcmamen.{Cities, Stall, StallRepo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.{Await, Future}
import com.seanmcapp.external.GoogleClient
import org.scalatest.time.Second

import scala.concurrent.duration.Duration

class MamenServiceSpec extends AsyncWordSpec with Matchers {
  
  val stallRepo = Mockito.mock(classOf[StallRepo])
  val googleClient = Mockito.mock(classOf[GoogleClient])

  val stallRepoData = Seq(
    Stall(1, "Ayam Suharti", "7JPF+GX2, Ps. Gn. Sitoli, Kec. Gunungsitoli, Kota Gunungsitoli, Sumatera Utara", Cities.Jakarta, "https://goo.gl/maps/dxcXghzHrhn96AZy6", "https://youtube.com", Some(-6.23), Some(106.67)),
    Stall(2, "Rm. Sederhana", "MRQJ+WR9, Aek Tolang, Kec. Pandan, Kabupaten Tapanuli Tengah, Sumatera Utara 22611", Cities.Jakarta, "https://goo.gl/maps/dxcXghzHrhn96AZy6", "https://youtube.com", Some(-6.23), Some(106.67)),
    Stall(3, "Warteg Bahari", "39VX+XG Gunung Sarik, Padang City, West Sumatra, Indonesia", Cities.Surabaya, "https://goo.gl/maps/dxcXghzHrhn96AZy6", "https://youtube.com", Some(-8.23), Some(110.67)),
    Stall(4, "Sate Madura Pak Kumis", "PJM8+72 Limbukan, Payakumbuh City, West Sumatra, Indonesia", Cities.Surabaya, "https://goo.gl/maps/dxcXghzHrhn96AZy6", "https://youtube.com", Some(-8.23), Some(110.67))
  )
  when(stallRepo.getAll).thenReturn(Future.successful(stallRepoData))
  
  val mamenService = new MamenService(stallRepo, googleClient)
  val mamenRequest = MamenRequest(MamenFilter())
  
  "search by name should return correct result" in {
    val request = MamenRequest(MamenFilter(name = Some("Warteg")))
    mamenService.search(request).map(_.length shouldBe 1)
  }

  "search by city id  should return correct result" in {
    val request = MamenRequest(MamenFilter(cityId = Some(2)))
    mamenService.search(request).map(_.length shouldBe 2)
  }

  "search by geolocation should return correct result" in {
    val request = MamenRequest(MamenFilter(geo = Some(GeoFilter(LatLng(-4.23, 112.67), LatLng(-8.23, 108.67)))))
    mamenService.search(request).map(_.length shouldBe 2)
  }

}
