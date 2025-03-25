package com.seanmcapp.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

object JwtUtil {
  private val appsConf = AppsConf()
  private val algorithm = Algorithm.HMAC256(appsConf.secretKey)

  def createToken(password: String): Option[String] = {
    if (password == appsConf.password) {
      val token = JWT.create()
        .withSubject(password)
        .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour expiration
        .sign(algorithm)
      Some(token)
    } else {
      None
    }
  }

  def validateToken(token: String): Option[Boolean] = {
    try {
      val verifier = JWT.require(algorithm).build()
      val decodedPassword = verifier.verify(token.replace("Bearer ", "")).getSubject
      Some(decodedPassword == appsConf.password)
    } catch {
      case _: JWTVerificationException => None
    }
  }
}
