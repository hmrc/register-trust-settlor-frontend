/*
 * Copyright 2021 HM Revenue & Customs
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
import controllers.living_settlor.routes
import controllers.trust_type.routes._
import models.{NormalMode, UserAnswers}
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.trust_type._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class TrustTypePrintHelper @Inject()(answerRowConverter: AnswerRowConverter) {

  def answerRows(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                (implicit messages: Messages): Seq[AnswerRow] = {

    val bound: answerRowConverter.Bound = answerRowConverter.bind(userAnswers, name)

    Seq(
      bound.yesNoQuestion(SetUpAfterSettlorDiedYesNoPage, "setUpAfterSettlorDied", SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId).url),
      bound.enumQuestion(KindOfTrustPage, "kindOfTrust", KindOfTrustController.onPageLoad(NormalMode, draftId).url, "kindOfTrust"),
      bound.yesNoQuestion(SetUpInAdditionToWillTrustYesNoPage, "setUpInAdditionToWillTrustYesNo", AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId).url),
      bound.enumQuestion(HowDeedOfVariationCreatedPage, "howDeedOfVariationCreated", HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId).url, "howDeedOfVariationCreated"),
      bound.yesNoQuestion(HoldoverReliefYesNoPage, "holdoverReliefYesNo", HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId).url),
      bound.yesNoQuestion(EfrbsYesNoPage, "employerFinancedRbsYesNo", EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId).url),
      bound.dateQuestion(EfrbsStartDatePage, "employerFinancedRbsStartDate", EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId).url),
      bound.enumQuestion(SettlorIndividualOrBusinessPage(index), "settlorIndividualOrBusiness", routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url, "settlorIndividualOrBusiness")
    ).flatten

  }

}
