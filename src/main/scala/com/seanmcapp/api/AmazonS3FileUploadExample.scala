package com.seanmcapp.api

import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import java.net.URL

import com.amazonaws.services.s3.model.ObjectMetadata
import com.seanmcapp.config.AWSConf
import com.seanmcapp.repository.mongodb.PhotoRepoImpl

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

object AmazonS3FileUploadExample {

  def ehek(): Future[String] = {
    val metadata = new ObjectMetadata()
    metadata.setContentType("image/jpeg")
    val awsConf = AWSConf()
    val credentials = new BasicAWSCredentials(awsConf.access, awsConf.secret)
    val amazonS3Client = AmazonS3ClientBuilder
      .standard
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion(awsConf.region)
      .build

    val photoRepoF = new PhotoRepoImpl
    for {
      photoRepo <- photoRepoF.getAll()
    } yield {
      var c = 0;
      photoRepo.foreach { photo =>
        Try(new URL(photo.thumbnailSrc).openStream()).toOption match {
          case Some(inputStream) =>
            val filename = "seanmcapp/" + photo.id + ".jpg"
            amazonS3Client.putObject(awsConf.bucket, filename, inputStream, metadata)
            c += 1
            println("[DONE "+c+"] " + photo.id)
          case _ =>
            val isDeleted = photoRepoF.delete(photo.id).isCompleted
            if (isDeleted) {
              println("[DELETED] " + photo.id)
            }
        }
      }
      "100"
    }
  }
}
