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

package navigation

import controllers.living_settlor.business.mld5.{routes => business5mldRoutes}
import controllers.living_settlor.business.{routes => businessRoutes}
import models.pages.KindOfTrust._
import models.{Mode, NormalMode, UserAnswers}
import pages._
import pages.living_settlor.business._
import pages.living_settlor.business.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import pages.trust_type._
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class BusinessSettlorNavigator extends Navigator {

  override def nextPage(page: Page,
                        mode: Mode,
                        draftId: String,
                        is5mldEnabled: Boolean = false): UserAnswers => Call = {

    route(draftId, is5mldEnabled)(page)
  }

  override protected def route(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorBusinessNamePage(index) => _ =>
      businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorBusinessUtrYesNoPage(index) => ua => yesNoNav(
      fromPage = SettlorBusinessUtrYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId),
      noCall = fiveMldYesNo(draftId, index, is5mldEnabled)(ua)
    )(ua)
    case SettlorBusinessUtrPage(index) => ua =>
      fiveMldYesNo(draftId, index, is5mldEnabled)(ua)
    case CountryOfResidenceYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = CountryOfResidenceYesNoPage(index),
        yesCall = business5mldRoutes.CountryOfResidenceInTheUkYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = fiveMldResumeJourney(draftId, index)(ua)
      )(ua)
    case CountryOfResidenceInTheUkYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = CountryOfResidenceInTheUkYesNoPage(index),
        yesCall = fiveMldResumeJourney(draftId, index)(ua),
        noCall = business5mldRoutes.CountryOfResidenceController.onPageLoad(NormalMode, index, draftId)
      )(ua)
    case CountryOfResidencePage(index) => ua =>
      fiveMldResumeJourney(draftId, index)(ua)
    case SettlorBusinessAddressYesNoPage(index) => ua => yesNoNav(
      fromPage = SettlorBusinessAddressYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
      noCall = displayAdditionalQuestionsForEmploymentTrusts(draftId, index)(ua)
    )(ua)
    case SettlorBusinessAddressUKYesNoPage(index) => yesNoNav(
      fromPage = SettlorBusinessAddressUKYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId),
      noCall = businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorBusinessAddressUKPage(index) =>
      displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessAddressInternationalPage(index) =>
      displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessTypePage(index) => _ =>
      businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorBusinessTimeYesNoPage(index) => _ =>
      businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
    case SettlorBusinessAnswerPage => _ =>
      controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def fiveMldYesNo(draftId: String, index: Int, is5mldEnabled: Boolean)(answers: UserAnswers): Call = {
    if (is5mldEnabled) {
      business5mldRoutes.CountryOfResidenceYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      fiveMldResumeJourney(draftId, index)(answers)
    }
  }

  private def fiveMldResumeJourney(draftId: String, index: Int)(answers: UserAnswers): Call = {
    yesNoNav(
      fromPage = SettlorBusinessUtrYesNoPage(index),
      yesCall = displayAdditionalQuestionsForEmploymentTrusts(draftId, index)(answers),
      noCall = businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId)
    )(answers)
  }

  private def displayAdditionalQuestionsForEmploymentTrusts(draftId: String, index: Int)(answers: UserAnswers): Call =
    answers.get(KindOfTrustPage) match {
      case Some(Employees) => businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId)
      case Some(_) => businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
}
