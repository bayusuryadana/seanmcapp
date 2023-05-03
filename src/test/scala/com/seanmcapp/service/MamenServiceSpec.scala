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
    Stall(1, "Ayam Suharti", "Rumah makan jual ayam goreng kremes", "Jl. Kapten tendean No.23, Jakarta Selatan", Cities.Jakarta, -6.23, 106.67, "https://youtube.com"),
    Stall(2, "Rm. Sederhana", "Masakan bundo ajo", "Jl. Balai Pustaka No.1, Jakarta Timur", Cities.Jakarta, -6.23, 106.67, "https://youtube.com"),
    Stall(3, "Warteg Bahari", "Masakan mahasiswa sehari-hari", "Jl. Desa Putra No.5 Jakarta Barat", Cities.Surabaya, -8.23, 110.67, "https://youtube.com"),
    Stall(4, "Sate Madura Pak Kumis", "Gue punya ayam nih. Bakar rumah lu yuk", "Jl. Mendoan selatan No.46 Jakarta Utara", Cities.Surabaya, -8.23, 110.67, "https://youtube.com")
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
