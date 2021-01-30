package com.seanmcapp.service

import com.seanmcapp.external.{HadithClient, HadithConstant, HadithDataResponse}

class HadithService(hadithClient: HadithClient) {

  def random: String = {
    val hadithDataResponse = hadithClient.random
    val hadithArabic = getHadithString(hadithDataResponse, HadithConstant.AR)
    val hadithEnglish = getHadithString(hadithDataResponse, HadithConstant.EN)
    val collection = hadithDataResponse.collection

    s"$hadithArabic\n\n$hadithEnglish\n\n$collection"
  }

  private def getHadithString(hadithDataResponse: HadithDataResponse, lang: String): String = {
    hadithDataResponse.hadith.find(_.lang == lang).map(_.body).getOrElse("Hadith not found")
  }

}
