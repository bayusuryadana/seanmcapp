package com.seanmcapp.service

import java.net.URLEncoder
import java.util.Base64

import com.seanmcapp.TwitterConf
import com.seanmcapp.external.{HeaderMap, HttpRequestClient}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import scala.util.Random

class TwitterService(httpClient: HttpRequestClient) {

  private val oauth_signature_method = "HMAC-SHA1"
  private val oauth_version = "1.0"

  private val idLength = 16
  private val config = TwitterConf()
  private val utf8 = "utf-8"

  private val baseUrlTweet = "https://api.twitter.com/2/users/:id/tweets"
  private val baseUrlLiked = "https://api.twitter.com/2/users/:id/liked_tweets"
  private val paramTweetFields = "tweet.fields=created_at,referenced_tweets"
  private val paramMaxResults = "max_results=10"

  private val accountMap = Map(
    "Alvida" -> "67603103",
    "Buggy" -> "159846549"
  )

  def get = {
    val oauth_consumer_key = config.consumerKey
    val oauth_nonce = generateId("", Random.alphanumeric.take(idLength))
    val oauth_timestamp = java.time.Instant.now.toEpochMilli / 1000
    val oauth_token = config.accessToken
    val paramString =
      s"""$paramMaxResults&
         |oauth_consumer_key=$oauth_consumer_key&
         |oauth_nonce=$oauth_nonce&
         |oauth_signature_method=$oauth_signature_method&
         |oauth_timestamp=$oauth_timestamp&
         |oauth_token=$oauth_token&
         |oauth_version=$oauth_version&
         |${paramTweetFields.replace(",","%2C")}"""
        .stripMargin
        .replaceAll("\\n", "")
        .replaceAll(" //[0-9]+", "")
    val encodedParamString = URLEncoder.encode(paramString, utf8)

    val authHeaderValue =
      s"""OAuth oauth_consumer_key="$oauth_consumer_key",
         |oauth_nonce="$oauth_nonce",
         |oauth_signature=":oauth_signature",
         |oauth_signature_method="$oauth_signature_method",
         |oauth_timestamp=$oauth_timestamp,
         |oauth_token=$oauth_token,
         |oauth_version=$oauth_version"""
        .stripMargin
        .replaceAll("\\n", "")
        .replaceAll(" //[0-9]+", "")

    accountMap.toSeq.map { case (name, id) =>
      val tweetResponse = generateSignatureAndSendRequest(baseUrlTweet, id, encodedParamString, authHeaderValue)
      val likedResponse = generateSignatureAndSendRequest(baseUrlLiked, id, encodedParamString, authHeaderValue)
      likedResponse
    }
  }

  private def generateSignatureAndSendRequest(templateUrl: String, id: String, encodedParamString: String, oAuthHeaderValue: String): String = {
    val baseUrl = templateUrl.replace(":id", id)
    val encodedBaseUrl = URLEncoder.encode(baseUrl, utf8)
    val signatureBaseString = s"GET&$encodedBaseUrl&$encodedParamString"
    val signingKey = new SecretKeySpec(s"${config.consumerSecret}&${config.accessSecret}".getBytes, "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(signingKey)
    val result: Array[Byte] = mac.doFinal(signatureBaseString.getBytes)
    val signatureResult = URLEncoder.encode(Base64.getEncoder.encodeToString(result), utf8)

    val headers = HeaderMap(Map("Authorization" -> oAuthHeaderValue.replace(":oauth_signature", signatureResult)))
    val fullUrl = s"$baseUrl?$paramTweetFields&$paramMaxResults"
    httpClient.sendGetRequest(fullUrl, Some(headers))
  }

  private def generateId(acc: String, s: LazyList[Char]): String = {
    if (s.isEmpty) acc
    else generateId(acc + s.head, s.tail)
  }

}
