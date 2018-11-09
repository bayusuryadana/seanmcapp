package com.seanmcapp.util

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.seanmcapp.config.AWSConf

object AWS {

  lazy val client = {
    val awsConf = AWSConf()
    val credentials = new BasicAWSCredentials(awsConf.access, awsConf.secret)
    AmazonS3ClientBuilder
      .standard
      .withCredentials(new AWSStaticCredentialsProvider(credentials))
      .withRegion(awsConf.region)
      .build
  }

  lazy val metadata = {
    val metadata = new ObjectMetadata()
    metadata.setContentType("image/jpeg")
    metadata
  }

}
