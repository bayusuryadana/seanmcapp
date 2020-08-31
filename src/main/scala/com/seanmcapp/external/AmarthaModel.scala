package com.seanmcapp.external

import org.joda.time.DateTime

// $COVERAGE-OFF$
case class AmarthaResponse[T](status: Int, code: Int, message: String, data: T)

case class AmarthaAuthData(accessToken: String, isActivated: Boolean, referrerUrl: String, name: String)

case class AmarthaAuthPayload(username: String, password: String)

case class AmarthaSummary(namaInvestor: Option[String],
                          investasiAktif: Long,
                          nilaiAset: Long,
                          paymentProgress: Double,
                          totalAllrevenue: Long,
                          totalFunds: Long
                         )

case class AmarthaTransaction(credit: String,
                              date: String, // dateformat "02 Oct 2019"
                              debit: String,
                              `type`: String)

object AmarthaTransactionType {
  val ROI = "Imbal Hasil" // debit
  val FUNDING = "Investasi" // credit
  val CASHOUT = "Cashout" // credit
  val REFUND = "Refund" // debit
  val TOP_UP = "Top-Up Via VA BCA" // debit
}

case class AmarthaMitraIdList(portofolio: List[AmarthaPortofolio])

case class AmarthaPortofolio(tenor: Int,
                             creditScoreGrade: String,
                             disbursementDate: String, // format 2019-07-18T07:00:00+07:00
                             isInsurance: Boolean,
                             isInsuranceRefund: Boolean,
                             isSharia: Boolean,
                             loanId: Long,
                             name: String,
                             plafond: Long,
                             stage: String, // ???
                             urlPic1: String,
                             urlPic2: String
                            )

object AmarthaPortofolioStageType {
  val AT_RISK = "ATRISK"
  val ON_TIME = "ONTIME"
  val END = "END"
  val END_EARLY = "END-EARLY"
  // (insurance claims)
  // (others)
}

case class AmarthaDetail(installment: List[AmarthaInstallment], summary: AmarthaSummaryDetail)

case class AmarthaInstallment(createdAt: String,
                              frequency: Int,
                              installmentType: String
                             )

case class AmarthaSummaryDetail(totalActiveRevenueProjection: Long)

object AmarthaInstallmentType {
  val NORMAL = "NORMAL"
  val GRACE_PERIOD = "TANPA-ANGSURAN"
  val NOT_PAY = "PAR"
  val COMPLETING = "DROPOUT"
}

////////////////////////////// MODIFIED MODEL ///////////////////////////////////////

case class AmarthaMitra(id: Long, name: String, detail: AmarthaLoanDetail, attribute: AmarthaAttribute,
                        installment: List[AmarthaInstallment])
object AmarthaMitra {
  def apply(portofolio: AmarthaPortofolio, detail: AmarthaDetail): AmarthaMitra = {
    val loanDetail: AmarthaLoanDetail = AmarthaLoanDetail(
      portofolio.tenor,
      detail.summary.totalActiveRevenueProjection,
      portofolio.plafond,
      getWeekYear(portofolio.disbursementDate)
    )
    val attribute = AmarthaAttribute(
      portofolio.isInsurance,
      portofolio.isSharia,
      portofolio.creditScoreGrade,
      portofolio.urlPic1,
      portofolio.urlPic2
    )
    val modifiedInstallment = detail.installment.map(i => i.copy(createdAt = getWeekYear(i.createdAt)))
    AmarthaMitra(portofolio.loanId, portofolio.name, loanDetail, attribute, modifiedInstallment)
  }

  def getWeekYear(dateTimeString: String): String = {
    val dateTime = new DateTime(dateTimeString)
    val year = dateTime.getWeekyear
    val weekOfTheYear = dateTime.getWeekOfWeekyear
    val weekString = if (weekOfTheYear >= 10) weekOfTheYear else s"0$weekOfTheYear"
    s"$year-$weekString"
  }
}

case class AmarthaLoanDetail(tenor: Int, ROI: Long, principal: Long, disbursementDate: String) {
  val ROIPercentage: Double = ROI.toDouble / principal.toDouble
  private val weeklyPrincipal: Long = principal / tenor
  private val weeklyReturn: Long = ROI / tenor
  val weeklyPayment: Long = weeklyPrincipal + weeklyReturn
}
case class AmarthaAttribute(isInsurance: Boolean, isSharia: Boolean, creditScore: String, urlPic1: String, urlPic2: String)