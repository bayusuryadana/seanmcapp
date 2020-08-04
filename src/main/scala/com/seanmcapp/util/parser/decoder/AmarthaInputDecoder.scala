package com.seanmcapp.util.parser.decoder

import spray.json.JsValue

case class AmarthaResponse(status: Int, code: Int, message: String, data: JsValue)

case class AmarthaAuthData(accessToken: String, isActivated: Boolean, referrerUrl: String, name: String)

case class AmarthaTransaction(borrowerName: String, // kosong semua anjing
                              credit: String,
                              date: String,
                              debit: String,
                              loanId: String,
                              saldo: String,
                              status: String, // SUCCESS semua anjing
                              transactionId: String, // useless
                              `type`: String)

object AmarthaType {
  val ROI = "Imbal Hasil" // debit
  val FUNDING = "Investasi" // credit
  val CASHOUT = "Cashout" // credit
  val REFUND = "Refund" // debit
  val TOP_UP = "Top-Up Via VA BCA" // debit
}

case class AmarthaPortofolio(//area: String, // always empty
                             at_risk: Boolean,
                             //branchName: String, // always empty
                             createdAt: String, // format 2019-07-14T11:15:45.217651+07:00
                             creditScoreGrade: String,
                             disbursementDate: String, // format 2019-07-18T07:00:00+07:00
                             installmentFrequency: Int, // number of payment done
                             isGracePeriod: Boolean, // for covid-90
                             isInsurance: Boolean,
                             isInsuranceRefund: Boolean,
                             isSharia: Boolean,
                             loanId: Long,
                             name: String,
                             plafond: Long, // total borrowing amount
                             purpose: String,
                             remainingPrincipal: Long,
                             // remark: String, // always empty
                             revenueProjection: Long,
                             sector: String, // always empty
                             stage: String, // ATRISK, ONTIME, END, END-EARLY, (insurance claims), (others)
                             submittedLoanDate: String, // format 2019-07-09T00:00:00+07:00
                             // tenor: Int, // always 50
                             // urlPic1: String,
                             // urlPic2: String
                            )

case class AmarthaMitra(portofolio: List[AmarthaPortofolio], total: Int)

trait AmarthaInputDecoder extends JsonDecoder {
  implicit val amarthaResponseFormat = jsonFormat4(AmarthaResponse)
  implicit val amarthaAuthDataFormat = jsonFormat4(AmarthaAuthData)
  implicit val amarthaTransactionFormat = jsonFormat9(AmarthaTransaction)
}
