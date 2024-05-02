/*
 * Copyright 2024 HM Revenue & Customs
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
import models.UserAnswers
import models.pages.KindOfTrust._
import pages._
import pages.living_settlor.business._
import pages.living_settlor.business.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import pages.trust_type._
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class BusinessSettlorNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String): UserAnswers => Call = route(draftId)(page)

  override protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call] =
    linearNavigation(draftId) orElse
      yesNoNavigation(draftId)

  private def linearNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorBusinessNamePage(index)                 =>
      ua =>
        if (ua.isTaxable) {
          businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(index, draftId)
        } else {
          business5mldRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
        }
    case SettlorBusinessUtrPage(index)                  =>
      _ => business5mldRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
    case CountryOfResidencePage(index)                  => ua => navigateToAddressQuestionsIfTaxableAndUtrUnknown(draftId, index)(ua)
    case SettlorBusinessAddressUKPage(index)            =>
      navigateToAnswersOrEmployeeTypeQuestions(draftId, index)
    case SettlorBusinessAddressInternationalPage(index) =>
      navigateToAnswersOrEmployeeTypeQuestions(draftId, index)
    case SettlorBusinessTypePage(index)                 =>
      _ => businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(index, draftId)
    case SettlorBusinessTimeYesNoPage(index)            =>
      _ => businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
    case SettlorBusinessAnswerPage                      => _ => controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorBusinessUtrYesNoPage(index)        =>
      ua =>
        yesNoNav(
          fromPage = SettlorBusinessUtrYesNoPage(index),
          yesCall = businessRoutes.SettlorBusinessUtrController.onPageLoad(index, draftId),
          noCall = business5mldRoutes.CountryOfResidenceYesNoController.onPageLoad(index, draftId)
        )(ua)
    case CountryOfResidenceYesNoPage(index)        =>
      ua =>
        yesNoNav(
          fromPage = CountryOfResidenceYesNoPage(index),
          yesCall = business5mldRoutes.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId),
          noCall = navigateToAddressQuestionsIfTaxableAndUtrUnknown(draftId, index)(ua)
        )(ua)
    case CountryOfResidenceInTheUkYesNoPage(index) =>
      ua =>
        yesNoNav(
          fromPage = CountryOfResidenceInTheUkYesNoPage(index),
          yesCall = navigateToAddressQuestionsIfTaxableAndUtrUnknown(draftId, index)(ua),
          noCall = business5mldRoutes.CountryOfResidenceController.onPageLoad(index, draftId)
        )(ua)
    case SettlorBusinessAddressYesNoPage(index)    =>
      ua =>
        yesNoNav(
          fromPage = SettlorBusinessAddressYesNoPage(index),
          yesCall = businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(index, draftId),
          noCall = navigateToAnswersOrEmployeeTypeQuestions(draftId, index)(ua)
        )(ua)
    case SettlorBusinessAddressUKYesNoPage(index)  =>
      yesNoNav(
        fromPage = SettlorBusinessAddressUKYesNoPage(index),
        yesCall = businessRoutes.SettlorBusinessAddressUKController.onPageLoad(index, draftId),
        noCall = businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(index, draftId)
      )
  }

  private def navigateToAddressQuestionsIfTaxableAndUtrUnknown(draftId: String, index: Int)(ua: UserAnswers): Call =
    if (ua.isTaxable) {
      yesNoNav(
        fromPage = SettlorBusinessUtrYesNoPage(index),
        yesCall = navigateToAnswersOrEmployeeTypeQuestions(draftId, index)(ua),
        noCall = businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(index, draftId)
      )(ua)
    } else {
      businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
    }

  private def navigateToAnswersOrEmployeeTypeQuestions(draftId: String, index: Int)(ua: UserAnswers): Call =
    ua.get(KindOfTrustPage) match {
      case Some(Employees) => businessRoutes.SettlorBusinessTypeController.onPageLoad(index, draftId)
      case Some(_)         => businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
      case None            => controllers.routes.SessionExpiredController.onPageLoad
    }
}
