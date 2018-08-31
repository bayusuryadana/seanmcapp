package com.seanmcapp.repository.mongodb

import com.seanmcapp.repository._
import com.mongodb.ConnectionString
import com.seanmcapp.config.MongoConf
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.connection.NettyStreamFactoryFactory
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}

import scala.reflect.ClassTag

abstract class DBComponent[T: ClassTag](collectionString: String) {

  val collection: MongoCollection[T] = DB.database.getCollection[T](collectionString)

}

object DB {

  private val connectionString = new ConnectionString(MongoConf().connectionString)

  private val settings = MongoClientSettings.builder().applyConnectionString(connectionString).streamFactoryFactory(NettyStreamFactoryFactory()).build()

  private val codecRegistry = fromRegistries(fromProviders(classOf[Customer], classOf[Photo], classOf[Vote], classOf[Account], classOf[Track]), DEFAULT_CODEC_REGISTRY)

  val database = MongoClient(settings).getDatabase(connectionString.getDatabase).withCodecRegistry(codecRegistry)

}