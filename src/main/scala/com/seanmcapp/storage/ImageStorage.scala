package com.seanmcapp.storage

import java.io.InputStream

import com.seanmcapp.config.StorageConf
import io.minio.{MinioClient, ObjectStat}

import scala.util.Try

trait ImageStorage {

  def put(filename: String, inputStream: InputStream): Option[Unit]

}

object ImageStorageImpl extends ImageStorage {

  private val storageConf = StorageConf()

  private val minioClient = new MinioClient(storageConf.host, storageConf.access, storageConf.secret)

  override def put(filename: String, inputStream: InputStream): Option[Unit] = {
    Try(minioClient.putObject(storageConf.bucket, "cbc/" + filename, inputStream, null, null, null, "image/jpeg")).toOption
  }

  // this function is only for testing
  def getMetadata(filename: String): ObjectStat = minioClient.statObject(storageConf.bucket, "cbc/"+ filename)
  def delete(filename: String): Unit = minioClient.removeObject(storageConf.bucket, "cbc/"+ filename)

}
