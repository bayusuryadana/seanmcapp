package com.seanmcapp

import com.seanmcapp.repository.dota._
import com.seanmcapp.repository.instagram._

import com.seanmcapp.repository.storage.{ImageStorage, ImageStorageImpl}
import com.seanmcapp.service.{CBCService, DotaService, InstagramFetcher}

trait Injection {

  val cbcAPI = new CBCService {
    override val photoRepo = PhotoRepoImpl
    override val customerRepo = CustomerRepoImpl
  }

  val dotaAPI = new DotaService {
    override val playerRepo = PlayerRepoImpl
    override val heroRepo = HeroRepoImpl
  }

  val instagramFetcher = new InstagramFetcher {
    override val photoRepo: PhotoRepo = PhotoRepoImpl
    override val imageStorage: ImageStorage = ImageStorageImpl
  }

}
