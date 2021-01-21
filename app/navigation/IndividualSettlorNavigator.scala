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
import uk.gov.hmrc.auth.core.AffinityGroup

import javax.inject.Singleton

@Singleton
class IndividualSettlorNavigator extends Navigator {

  override def nextPage(page: Page,
                        mode: Mode,
                        draftId: String,
                        af: AffinityGroup = AffinityGroup.Organisation,
                        is5mldEnabled: Boolean = false): UserAnswers => Call = {

    route(draftId, is5mldEnabled)(page)(af)
  }

  override protected def route(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId) orElse
      mldDependentSimpleNavigation(draftId, is5mldEnabled) orElse
      mldDependentYesNoNavigation(draftId, is5mldEnabled)
  }

  private def simpleNavigation(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorIndividualNamePage(index) => _ => _ =>
      SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case CountryOfNationalityPage(index) => _ => _ =>
      SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    case CountryOfResidencyPage(index) => _ => ua =>
      navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
    case SettlorAddressUKPage(index) => _ => _ =>
      SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorAddressInternationalPage(index) => _ => _ =>
      SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case MentalCapacityYesNoPage(index) => _ =>_ =>
      SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualAnswerPage => _ => _ =>
      controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case page @ CountryOfNationalityYesNoPage(index) => _ =>
      yesNoNav(
        fromPage = page,
        yesCall = UkCountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ UkCountryOfNationalityYesNoPage(index) => _ =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = CountryOfNationalityController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ CountryOfResidencyYesNoPage(index) => _ => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = UkCountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
      )(ua)
    case page @ UkCountryOfResidencyYesNoPage(index) => _ => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId),
        noCall = CountryOfResidencyController.onPageLoad(NormalMode, index, draftId)
      )(ua)
    case page @ SettlorAddressUKYesNoPage(index) => _ =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ SettlorIndividualPassportYesNoPage(index) => _ =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId)
      )
  }

  private def mldDependentSimpleNavigation(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorIndividualDateOfBirthPage(index) => _ => _ =>
      navigateAwayFromDateOfBirthQuestions(is5mldEnabled, index, draftId)
    case SettlorIndividualNINOPage(index) => _ => _ =>
      if (is5mldEnabled) {
        CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
      } else {
        SettlorIndividualAnswerController.onPageLoad(index, draftId)
      }
    case SettlorIndividualPassportPage(index) => _ => _ =>
      navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
    case SettlorIndividualIDCardPage(index) => _ => _ =>
      navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
  }

  private def mldDependentYesNoNavigation(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case page @ SettlorIndividualDateOfBirthYesNoPage(index) => _ =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromDateOfBirthQuestions(is5mldEnabled, index, draftId)
      )
    case page @ SettlorIndividualNINOYesNoPage(index) => _ =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId),
        noCall = if (is5mldEnabled) {
          CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
        } else {
          SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
        }
      )
    case page @ SettlorAddressYesNoPage(index) => _ => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateToAnswersOrLegallyIncapable(is5mldEnabled, index, draftId)
      )(ua)
    case page @ SettlorIndividualIDCardYesNoPage(index) => _ =>
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
