package com.seanmcapp.external

// $COVERAGE-OFF$
case class AmarthaResponse[T](status: Int, code: Int, message: String, data: T)

case class AmarthaAuthData(accessToken: String, isActivated: Boolean, referrerUrl: String, name: String)

case class AmarthaAuthPayload(username: String, password: String)

case class AmarthaTransaction(credit: String,
                              date: String, // dateformat "02 Oct 2019"
                              debit: String,
                              `type`: String,
                              saldo: String
                             )

object AmarthaTransactionType {
  val ROI = "Imbal Hasil" // debit
  val FUNDING = "Investasi" // credit
  val CASHOUT = "Cashout" // credit
  val REFUND = "Refund" // debit
  val TOP_UP = "Top-Up Via VA BCA" // debit
}
