package com.seanmcapp.repository

import java.io.{File, FileInputStream}

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

class FileRepoSpec extends AsyncWordSpec with Matchers {

  "put function should properly store inside storage" in {
    val filename = "The International 2019.jpg"
    val inputStream = new FileInputStream(new File("src/it/resources/" + filename))
    FileRepoImpl.put(filename, inputStream)

    val res = FileRepoImpl.getMetadata(filename)
    FileRepoImpl.delete(filename)
    res.bucketName shouldBe "seanmcapp"
    res.name shouldBe "cbc/The International 2019.jpg"
    res.contentType shouldBe "image/jpeg"
    res.length shouldBe 1234895

  }

}
