/*
 * Copyright 2026 HM Revenue & Customs
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

import com.google.inject.Inject
import controllers.trust_type.routes._
import models.UserAnswers
import pages.trust_type._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class TrustTypePrintHelper @Inject() (answerRowConverter: AnswerRowConverter) {

  def answerRows(userAnswers: UserAnswers, draftId: String, index: Int = 0)(implicit
    messages: Messages
  ): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers)

    Seq(
      bound.yesNoQuestion(
        SetUpByLivingSettlorYesNoPage,
        "setUpByLivingSettlorYesNo",
        SetUpByLivingSettlorController.onPageLoad(draftId, index).url
      ),
      bound.enumQuestion(
        KindOfTrustPage,
        "kindOfTrust",
        KindOfTrustController.onPageLoad(draftId, index).url,
        "kindOfTrust"
      ),
      bound.yesNoQuestion(
        SetUpInAdditionToWillTrustYesNoPage,
        "setUpInAdditionToWillTrustYesNo",
        AdditionToWillTrustYesNoController.onPageLoad(draftId, index).url
      ),
      bound.enumQuestion(
        HowDeedOfVariationCreatedPage,
        "howDeedOfVariationCreated",
        HowDeedOfVariationCreatedController.onPageLoad(draftId, index).url,
        "howDeedOfVariationCreated"
      ),
      bound.yesNoQuestion(
        HoldoverReliefYesNoPage,
        "holdoverReliefYesNo",
        HoldoverReliefYesNoController.onPageLoad(draftId, index).url
      ),
      bound.yesNoQuestion(
        EfrbsYesNoPage,
        "employerFinancedRbsYesNo",
        EmployerFinancedRbsYesNoController.onPageLoad(draftId, index).url
      ),
      bound.dateQuestion(
        EfrbsStartDatePage,
        "employerFinancedRbsStartDate",
        EmployerFinancedRbsStartDateController.onPageLoad(draftId, index).url
      )
    ).flatten

  }

}
