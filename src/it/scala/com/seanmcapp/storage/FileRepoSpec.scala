package com.seanmcapp.storage

import java.io.{File, FileInputStream}

import org.scalatest.wordspec.AsyncWordSpec
import org.scalatest.matchers.should.Matchers

class FileRepoSpec extends AsyncWordSpec with Matchers {

  "put function should properly store inside storage" in {
    val filename = "The International 2019.jpg"
    val inputStream = new FileInputStream(new File("src/it/resources/" + filename))
    FileStorageImpl$.put(filename, inputStream)

    val res = FileStorageImpl$.getMetadata(filename)
    FileStorageImpl$.delete(filename)
    res.bucketName shouldBe "seanmcapp"
    res.name shouldBe "cbc/The International 2019.jpg"
    res.contentType shouldBe "image/jpeg"
    res.length shouldBe 1234895

  }

}
