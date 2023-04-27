/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils.print

import base.SpecBase
import controllers.trust_type.routes._
import models.UserAnswers
import models.pages.IndividualOrBusiness._
import models.pages._
import org.scalatest.Assertion
import pages.trust_type._
import play.twirl.api.Html
import viewmodels.AnswerRow

import java.time.LocalDate

class TrustTypePrintHelperSpec extends SpecBase {

  private val trustTypePrintHelper = injector.instanceOf[TrustTypePrintHelper]

  private val date: LocalDate = LocalDate.parse("1996-02-03")

  "LivingSettlorPrintHelper" must {

    "return answer rows" when {

      "deed of variation set up to replace will trust" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpByLivingSettlorYesNoPage, false)
          .success
          .value
          .set(KindOfTrustPage, KindOfTrust.Deed)
          .success
          .value
          .set(SetUpInAdditionToWillTrustYesNoPage, false)
          .success
          .value
          .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill)(DeedOfVariation.writes)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "setUpByLivingSettlorYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SetUpByLivingSettlorController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "kindOfTrust.checkYourAnswersLabel",
            answer = Html("A trust through a Deed of Variation or family agreement"),
            changeUrl = Some(KindOfTrustController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "setUpInAdditionToWillTrustYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(AdditionToWillTrustYesNoController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "howDeedOfVariationCreated.checkYourAnswersLabel",
            answer = Html("to replace a will trust"),
            changeUrl = Some(HowDeedOfVariationCreatedController.onPageLoad(fakeDraftId).url)
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "intervivos" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpByLivingSettlorYesNoPage, false)
          .success
          .value
          .set(KindOfTrustPage, KindOfTrust.Intervivos)
          .success
          .value
          .set(HoldoverReliefYesNoPage, true)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "setUpByLivingSettlorYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SetUpByLivingSettlorController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "kindOfTrust.checkYourAnswersLabel",
            answer = Html("A trust created during their lifetime to gift or transfer assets"),
            changeUrl = Some(KindOfTrustController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "holdoverReliefYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(HoldoverReliefYesNoController.onPageLoad(fakeDraftId).url)
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "flat management" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpByLivingSettlorYesNoPage, false)
          .success
          .value
          .set(KindOfTrustPage, KindOfTrust.FlatManagement)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "setUpByLivingSettlorYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SetUpByLivingSettlorController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "kindOfTrust.checkYourAnswersLabel",
            answer = Html("A trust for a building or building with tenants"),
            changeUrl = Some(KindOfTrustController.onPageLoad(fakeDraftId).url)
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "heritage maintenance fund" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpByLivingSettlorYesNoPage, false)
          .success
          .value
          .set(KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "setUpByLivingSettlorYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SetUpByLivingSettlorController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "kindOfTrust.checkYourAnswersLabel",
            answer = Html("A trust for the repair of historic buildings"),
            changeUrl = Some(KindOfTrustController.onPageLoad(fakeDraftId).url)
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "trust for employees of a company (EFRBS)" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpByLivingSettlorYesNoPage, false)
          .success
          .value
          .set(KindOfTrustPage, KindOfTrust.Employees)
          .success
          .value
          .set(EfrbsYesNoPage, true)
          .success
          .value
          .set(EfrbsStartDatePage, date)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "setUpByLivingSettlorYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SetUpByLivingSettlorController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "kindOfTrust.checkYourAnswersLabel",
            answer = Html("A trust for the employees of a company"),
            changeUrl = Some(KindOfTrustController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "employerFinancedRbsYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(EmployerFinancedRbsYesNoController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "employerFinancedRbsStartDate.checkYourAnswersLabel",
            answer = Html("3 February 1996"),
            changeUrl = Some(EmployerFinancedRbsStartDateController.onPageLoad(fakeDraftId).url)
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }
    }
  }

  private def assertThatUserAnswersProduceExpectedAnswerRows(
    userAnswers: UserAnswers,
    expectedAnswerRows: Seq[AnswerRow]
  ): Assertion = {

    val answerRows = trustTypePrintHelper.answerRows(userAnswers, fakeDraftId)
    answerRows mustEqual expectedAnswerRows
  }
}
