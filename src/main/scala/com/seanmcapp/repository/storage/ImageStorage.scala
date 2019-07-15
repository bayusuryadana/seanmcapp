package com.seanmcapp.repository.storage

import java.io.InputStream

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.seanmcapp.config.AWSConf

trait ImageStorage {

  def put(filename: String, inputStream: InputStream): Unit

}

object ImageStorageImpl extends ImageStorage {

  private val awsConf = AWSConf()

  private lazy val client = {
    val credentials = new BasicAWSCredentials(awsConf.access, awsConf.secret)
    AmazonS3ClientBuilder.standard.withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion(awsConf.region).build
  }

  private lazy val metadata = {
    val metadata = new ObjectMetadata()
    metadata.setContentType("image/jpeg")
    metadata
  }

  override def put(filename:String, inputStream: InputStream): Unit = {
    client.putObject(awsConf.bucket, filename, inputStream, metadata)
  }

}
