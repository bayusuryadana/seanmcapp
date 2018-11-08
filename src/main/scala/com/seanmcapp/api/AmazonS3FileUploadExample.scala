package com.seanmcapp.api

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import java.net.URL

import com.amazonaws.services.s3.model.ObjectMetadata
import com.seanmcapp.config.AWSConf
import com.seanmcapp.repository.mongodb.PhotoRepoImpl

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AmazonS3FileUploadExample {

  def ehek(): Future[String] = {
    for {
      photoRepo <- new PhotoRepoImpl().getAll()
    } yield {
      val metadata = new ObjectMetadata()
      metadata.setContentType("image/jpeg")
      println("content-type => " + metadata.getContentType)
      val awsConf = AWSConf()
      val credentials = new BasicAWSCredentials(awsConf.access, awsConf.secret)
      val amazonS3Client = AmazonS3ClientBuilder
        .standard
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withRegion(awsConf.region)
        .build
      photoRepo.map { photo =>
        val file = new URL(photo.thumbnailSrc).openStream()
        val filename = "seanmcapp/" + photo.id + ".jpg"
        amazonS3Client.putObject(awsConf.bucket, filename, file, metadata)
        println("[DONE] " + photo.id)
      }
      "100"
    }
  }
}
