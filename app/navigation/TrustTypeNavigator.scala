/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation

import controllers.deceased_settlor.routes._
import controllers.living_settlor.routes._
import controllers.routes._
import controllers.trust_type.routes._
import models.UserAnswers
import models.pages.KindOfTrust._
import pages._
import pages.trust_type._
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class TrustTypeNavigator extends Navigator {

  override def nextPage(page: Page,
                        draftId: String): UserAnswers => Call = route(draftId)(page)

  override protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SetUpAfterSettlorDiedYesNoPage => ua => yesNoNav(
      fromPage = SetUpAfterSettlorDiedYesNoPage,
      yesCall = SettlorsNameController.onPageLoad(draftId),
      noCall = if (ua.isTaxable) {
        KindOfTrustController.onPageLoad(draftId)
      } else {
        redirectToLivingSettlorQuestions(draftId)
      }
    )(ua)
    case KindOfTrustPage => kindOfTrustPage(draftId)
    case EfrbsYesNoPage => yesNoNav(
      fromPage = EfrbsYesNoPage,
      yesCall = EmployerFinancedRbsStartDateController.onPageLoad(draftId),
      noCall = redirectToLivingSettlorQuestions(draftId)
    )
    case SetUpInAdditionToWillTrustYesNoPage => yesNoNav(
      fromPage = SetUpInAdditionToWillTrustYesNoPage,
      yesCall = SettlorsNameController.onPageLoad(draftId),
      noCall = HowDeedOfVariationCreatedController.onPageLoad(draftId)
    )
    case EfrbsStartDatePage | HowDeedOfVariationCreatedPage | HoldoverReliefYesNoPage => _ =>
      redirectToLivingSettlorQuestions(draftId)
  }

  private def kindOfTrustPage(draftId: String)(answers: UserAnswers): Call = {
    answers.get(KindOfTrustPage) match {
      case Some(Deed) =>
        AdditionToWillTrustYesNoController.onPageLoad(draftId)
      case Some(Intervivos) =>
        HoldoverReliefYesNoController.onPageLoad(draftId)
      case Some(FlatManagement) | Some(HeritageMaintenanceFund) =>
        redirectToLivingSettlorQuestions(draftId)
      case Some(Employees) =>
        EmployerFinancedRbsYesNoController.onPageLoad(draftId)
      case _ =>
        SessionExpiredController.onPageLoad
    }
  }

  private def redirectToLivingSettlorQuestions(draftId: String): Call = {
    SettlorIndividualOrBusinessController.onPageLoad(0, draftId)
  }
}
