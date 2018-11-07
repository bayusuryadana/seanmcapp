package com.seanmcapp.api

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import java.io.File

import com.seanmcapp.config.AWSConf

object AmazonS3FileUploadExample {

  def ehek(): String = {
    val fileToUpload = new File("ehek.jpg")
    val filename = "seanmcapp/wow.jpg"

    val awsConf = AWSConf()
    val credentials = new BasicAWSCredentials(awsConf.access, awsConf.secret)
    val amazonS3Client = AmazonS3ClientBuilder
      .standard
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion(awsConf.region)
      .build
    amazonS3Client.putObject(awsConf.bucket, filename, fileToUpload)
    "135"
  }

}
