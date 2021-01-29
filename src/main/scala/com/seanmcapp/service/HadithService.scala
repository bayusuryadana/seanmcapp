package com.seanmcapp.service

import com.seanmcapp.external.{HadithClient, HadithDataResponse}

class HadithService(hadithClient: HadithClient) {

  def random: HadithDataResponse = {
    hadithClient.random
  }

}
