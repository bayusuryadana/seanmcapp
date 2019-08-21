package com.seanmcapp

import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.service.{CBCService, DotaService, InstagramFetcher}
import com.seanmcapp.storage.ImageStorageImpl
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl

trait Injection {

  val cbcAPI = new CBCService(PhotoRepoImpl, CustomerRepoImpl, HttpRequestBuilderImpl)

  val dotaAPI = new DotaService(PlayerRepoImpl, HeroRepoImpl, HttpRequestBuilderImpl)

  val instagramFetcher = new InstagramFetcher(PhotoRepoImpl, ImageStorageImpl, HttpRequestBuilderImpl)

}
