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

import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import models.{Mode, NormalMode, UserAnswers}
import pages._
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class IndividualSettlorNavigator extends Navigator {

  override def nextPage(page: Page,
                        mode: Mode,
                        draftId: String,
                        is5mldEnabled: Boolean = false): UserAnswers => Call = {

    route(draftId, is5mldEnabled)(page)
  }

  override protected def route(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId) orElse
      mldDependentSimpleNavigation(draftId, is5mldEnabled) orElse
      mldDependentYesNoNavigation(draftId, is5mldEnabled)
  }

  private def simpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorIndividualNamePage(index) => _ =>
      SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case CountryOfNationalityPage(index) => _ =>
      SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    case CountryOfResidencyPage(index) => ua =>
      navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
    case SettlorAddressUKPage(index) => _ =>
      SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorAddressInternationalPage(index) => _ =>
      SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case MentalCapacityYesNoPage(index) => _ =>
      SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualAnswerPage => _ =>
      controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case page @ CountryOfNationalityYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = UkCountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ UkCountryOfNationalityYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = CountryOfNationalityController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ CountryOfResidencyYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = UkCountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
      )(ua)
    case page @ UkCountryOfResidencyYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId),
        noCall = CountryOfResidencyController.onPageLoad(NormalMode, index, draftId)
      )(ua)
    case page @ SettlorAddressUKYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ SettlorIndividualPassportYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId)
      )
  }

  private def mldDependentSimpleNavigation(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorIndividualDateOfBirthPage(index) => _ =>
      navigateAwayFromDateOfBirthQuestions(is5mldEnabled, index, draftId)
    case SettlorIndividualNINOPage(index) => _ =>
      if (is5mldEnabled) {
        CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
      } else {
        SettlorIndividualAnswerController.onPageLoad(index, draftId)
      }
    case SettlorIndividualPassportPage(index) => _ =>
      navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
    case SettlorIndividualIDCardPage(index) => _ =>
      navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
  }

  private def mldDependentYesNoNavigation(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    case page @ SettlorIndividualDateOfBirthYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromDateOfBirthQuestions(is5mldEnabled, index, draftId)
      )
    case page @ SettlorIndividualNINOYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId),
        noCall = if (is5mldEnabled) {
          CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
        } else {
          SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
        }
      )
    case page @ SettlorAddressYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
      )(ua)
    case page @ SettlorIndividualIDCardYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
      )
  }

  private def navigateAwayFromDateOfBirthQuestions(is5mldEnabled: Boolean, index: Int, draftId: String): Call = {
    if (is5mldEnabled) {
      CountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(userAnswers: UserAnswers, index: Int, draftId: String): Call = {
    if (isNinoDefined(userAnswers, index)) {
      MentalCapacityYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
    }
  }

  private def navigateToAnswersOrLegallyIncapable(is5mldEnabled: Boolean, index: Int, draftId: String): Call = {
    if (is5mldEnabled) {
      MentalCapacityYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      SettlorIndividualAnswerController.onPageLoad(index, draftId)
    }
  }

  private def isNinoDefined(userAnswers: UserAnswers, index: Int): Boolean = {
    userAnswers.get(SettlorIndividualNINOPage(index)).isDefined
  }
}
