package com.seanmcapp

import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._
import com.seanmcapp.repository.seanmcwallet.WalletRepoImpl
import com.seanmcapp.service.{CBCService, DotaService, InstagramFetcher, WalletService}
import com.seanmcapp.storage.ImageStorageImpl
import com.seanmcapp.util.requestbuilder.HttpRequestBuilderImpl

trait Injection {

  val cbcAPI = new CBCService(PhotoRepoImpl, CustomerRepoImpl, HttpRequestBuilderImpl)

  val dotaAPI = new DotaService(PlayerRepoImpl, HeroRepoImpl, HeroAttributeRepoImpl, HttpRequestBuilderImpl)

  val instagramFetcher = new InstagramFetcher(PhotoRepoImpl, ImageStorageImpl, HttpRequestBuilderImpl)

  val walletAPI = new WalletService(WalletRepoImpl)

}
