package com.seanmcapp

import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{provide, redirect}
import com.softwaremill.session.{SessionConfig, SessionManager}
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.softwaremill.session._

import scala.concurrent.ExecutionContext.Implicits.global

trait Session {

  val sessionConfig = SessionConfig.default("ZpCTHcYxSobOw3QcPGvabX7qCymLFzWzw9bWiz2rNge4kSNiDsl7XRnt5c2Hr1qK")
  implicit val sessionManager = new SessionManager[String](sessionConfig)
  implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[String] {
    def log(msg: String):Unit = println(msg)
  }

  def mySetSession(v: String) = setSession(refreshable, usingCookies, v)
  def myRequiredSession: Directive1[String] = optionalSession(refreshable, usingCookies).flatMap {
    case None       => redirect("/wallet/login", StatusCodes.PermanentRedirect)
    case Some(data) => provide(data)
  }
  val myInvalidateSession = invalidateSession(refreshable, usingCookies)

}
