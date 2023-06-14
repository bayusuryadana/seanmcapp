package com.seanmcapp.service

import com.seanmcapp.repository.seanmcmamen.{Cities, Stall, StallRepo}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class MamenServiceSpec extends AsyncWordSpec with Matchers {
  
  val stallRepo = Mockito.mock(classOf[StallRepo])
  val stallRepoData = Seq(
    Stall(1, "Ayam Suharti", "7JPF+GX2, Ps. Gn. Sitoli, Kec. Gunungsitoli, Kota Gunungsitoli, Sumatera Utara", Cities.Jakarta, "https://youtube.com", "https://goo.gl/maps/dxcXghzHrhn96AZy6", Some(-6.23), Some(106.67), None),
    Stall(2, "Rm. Sederhana", "MRQJ+WR9, Aek Tolang, Kec. Pandan, Kabupaten Tapanuli Tengah, Sumatera Utara 22611", Cities.Jakarta, "https://youtube.com", "https://goo.gl/maps/dxcXghzHrhn96AZy6", Some(-6.23), Some(106.67), None),
    Stall(3, "Warteg Bahari", "39VX+XG Gunung Sarik, Padang City, West Sumatra, Indonesia", Cities.Surabaya, "https://youtube.com", "https://goo.gl/maps/dxcXghzHrhn96AZy6", Some(-8.23), Some(110.67), None),
    Stall(4, "Sate Madura Pak Kumis", "PJM8+72 Limbukan, Payakumbuh City, West Sumatra, Indonesia", Cities.Surabaya, "https://youtube.com", "https://goo.gl/maps/dxcXghzHrhn96AZy6", Some(-8.23), Some(110.67), None)
  )
  when(stallRepo.getAll).thenReturn(Future.successful(stallRepoData))
  
  val mamenService = new MamenService(stallRepo)
  
  "searchByNameOrDescription should return correct result" in {
    mamenService.searchByNameOrDescription("Masakan").map(_.length shouldBe 2)
  }

  "searchByCity should return correct result" in {
    mamenService.searchByCity(2).map(_.length shouldBe 2)
  }

  "searchByGeo should return correct result" in {
    mamenService.searchByGeo(-6.23, 110.67, 2).map(_.length shouldBe 2)
  }

}
