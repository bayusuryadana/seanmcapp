package com.seanmcapp.external

// $COVERAGE-OFF$
case class AmarthaResponse[T](status: Int, code: Int, message: String, data: T)

case class AmarthaAuthData(accessToken: String, isActivated: Boolean, referrerUrl: String, name: String)

case class AmarthaAuthPayload(username: String, password: String)

case class AmarthaSummary(namaInvestor: Option[String],
                          angsuranPokokAktif: Long,
                          cashout: Long, // can count from transaction
                          danaInvestasi: Long, // can count from transaction
                          invest: Long,
                          investasiAktif: Long,
                          jumlahInvestasi: Long,
                          nilaiAset: Long,
                          paymentProgress: Double,
                          sisaActiveRevenue: Long,
                          sisaPokokAktif: Long,
                          totalActiveRevenueProjection: Long,
                          totalAllrevenue: Long,
                          totalFunds: Long
                         )

case class AmarthaTransaction(borrowerName: Option[String],
                              credit: String,
                              date: String, // dateformat "02 Oct 2019"
                              debit: String,
                              loanId: String,
                              transactionId: String,
                              `type`: String)

object AmarthaTransactionType {
  val ROI = "Imbal Hasil" // debit
  val FUNDING = "Investasi" // credit
  val CASHOUT = "Cashout" // credit
  val REFUND = "Refund" // debit
  val TOP_UP = "Top-Up Via VA BCA" // debit
}

case class AmarthaPortofolio(area: Option[String],
                             branchName: Option[String],
                             createdAt: String, // format 2019-07-14T11:15:45.217651+07:00
                             creditScoreGrade: String,
                             disbursementDate: String, // format 2019-07-18T07:00:00+07:00
                             dueDate: Option[String],
                             isInsurance: Boolean,
                             isInsuranceRefund: Boolean,
                             isSharia: Boolean,
                             installment: Option[List[AmarthaInstallment]],
                             loanId: Long,
                             name: String,
                             plafond: Long,
                             provinceName: Option[String],
                             purpose: String,
                             revenueProjection: Long,
                             scheduleDay: Option[String],
                             sector: Option[String],
                             stage: String, // ATRISK, ONTIME, END, END-EARLY, (insurance claims), (others)
                             submittedLoanDate: String, // format 2019-07-09T00:00:00+07:00
                             urlPic1: String,
                             urlPic2: String
                            )

object AmarthaPortofolioStageType {
  val AT_RISK = "ATRISK"
  val ON_TIME = "ONTIME"
  val END = "END"
  val END_EARLY = "END-EARLY"
}

case class AmarthaMitraIdList(portofolio: List[AmarthaPortofolio])

case class AmarthaInstallment(createdAt: String, // datetime format "2020-06-25T19:20:48.912346+07:00"
                              frequency: Int, // number of payment done in this record
                              installmentType: String, // NORMAL, TANPA-ANGSURAN - TODO: useless status?
                              isGracePeriod: Boolean,
                              pembayaranImbalHasil: Long,
                              pembayaranPokokInvestasi: Long
                             )

case class AmarthaLoan(_id: Long,
                       areaName: String,
                       branchName: String,
                       dueDate: String, // datetime "2020-08-06T07:00:00+07:00"
                       provinceName: String,
                       scheduleDay: String,
                       sector: String
                      )

case class AmarthaDetail(installment: List[AmarthaInstallment], loan: AmarthaLoan)

case class AmarthaResult(summary: AmarthaSummary, mitra: List[AmarthaPortofolio], transaction: List[AmarthaTransaction])