package com.seanmcapp.service

import com.seanmcapp.repository.seanmcmamen.{Cities, Diner, DinerRepo}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future

class MamenServiceSpec extends AsyncWordSpec with Matchers {
  
  val dinerRepo = Mockito.mock(classOf[DinerRepo])
  val dinerRepoData = Seq(
    Diner(1, "Ayam Suharti", "Rumah makan jual ayam goreng kremes", "Jl. Kapten tendean No.23, Jakarta Selatan", Cities.Jakarta, -6.23, 106.67, "https://youtube.com"),
    Diner(2, "Rm. Sederhana", "Masakan bundo ajo", "Jl. Balai Pustaka No.1, Jakarta Timur", Cities.Jakarta, -6.23, 106.67, "https://youtube.com"),
    Diner(3, "Warteg Bahari", "Masakan mahasiswa sehari-hari", "Jl. Desa Putra No.5 Jakarta Barat", Cities.Surabaya, -8.23, 110.67, "https://youtube.com"),
    Diner(4, "Sate Madura Pak Kumis", "Gue punya ayam nih. Bakar rumah lu yuk", "Jl. Mendoan selatan No.46 Jakarta Utara", Cities.Surabaya, -8.23, 110.67, "https://youtube.com")
  )
  when(dinerRepo.getAll).thenReturn(Future.successful(dinerRepoData))
  
  val mamenService = new MamenService(dinerRepo)
  
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
