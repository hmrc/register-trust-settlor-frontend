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

package navigation

import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import models.UserAnswers
import pages._
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class IndividualSettlorNavigator extends Navigator {

  override def nextPage(page: Page, draftId: String): UserAnswers => Call = route(draftId)(page)

  override protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call] =
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId) orElse
      mldDependentSimpleNavigation(draftId) orElse
      mldDependentYesNoNavigation(draftId)

  private def simpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorAliveYesNoPage(index)           => _ => SettlorIndividualNameController.onPageLoad(index, draftId)
    case SettlorIndividualNamePage(index)       => _ => SettlorIndividualDateOfBirthYesNoController.onPageLoad(index, draftId)
    case CountryOfNationalityPage(index)        =>
      ua => navigateAwayFromCountryOfNationalityQuestions(ua.isTaxable, index, draftId)
    case CountryOfResidencyPage(index)          => ua => navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
    case SettlorAddressUKPage(index)            => _ => SettlorIndividualPassportYesNoController.onPageLoad(index, draftId)
    case SettlorAddressInternationalPage(index) =>
      _ => SettlorIndividualPassportYesNoController.onPageLoad(index, draftId)
    case MentalCapacityYesNoPage(index)         => _ => SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualAnswerPage            => _ => controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case page @ CountryOfNationalityYesNoPage(index)      =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = UkCountryOfNationalityYesNoController.onPageLoad(index, draftId),
          noCall = navigateAwayFromCountryOfNationalityQuestions(ua.isTaxable, index, draftId)
        )(ua)
    case page @ UkCountryOfNationalityYesNoPage(index)    =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = navigateAwayFromCountryOfNationalityQuestions(ua.isTaxable, index, draftId),
          noCall = CountryOfNationalityController.onPageLoad(index, draftId)
        )(ua)
    case page @ CountryOfResidencyYesNoPage(index)        =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = UkCountryOfResidencyYesNoController.onPageLoad(index, draftId),
          noCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
        )(ua)
    case page @ UkCountryOfResidencyYesNoPage(index)      =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId),
          noCall = CountryOfResidencyController.onPageLoad(index, draftId)
        )(ua)
    case page @ SettlorAddressUKYesNoPage(index)          =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKController.onPageLoad(index, draftId),
        noCall = SettlorIndividualAddressInternationalController.onPageLoad(index, draftId)
      )
    case page @ SettlorIndividualPassportYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualPassportController.onPageLoad(index, draftId),
        noCall = SettlorIndividualIDCardYesNoController.onPageLoad(index, draftId)
      )
  }

  private def mldDependentSimpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorIndividualDateOfBirthPage(index) => _ => CountryOfNationalityYesNoController.onPageLoad(index, draftId)
    case SettlorIndividualNINOPage(index)        => _ => CountryOfResidencyYesNoController.onPageLoad(index, draftId)
    case SettlorIndividualPassportPage(index)    => ua => showOrHideMentalCapacityQuestion(ua, index, draftId)
    case SettlorIndividualIDCardPage(index)      => ua => showOrHideMentalCapacityQuestion(ua, index, draftId)
  }

  private def mldDependentYesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case page @ SettlorIndividualDateOfBirthYesNoPage(index) =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = SettlorIndividualDateOfBirthController.onPageLoad(index, draftId),
          noCall = CountryOfNationalityYesNoController.onPageLoad(index, draftId)
        )(ua)
    case page @ SettlorIndividualNINOYesNoPage(index)        =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = SettlorIndividualNINOController.onPageLoad(index, draftId),
          noCall = CountryOfResidencyYesNoController.onPageLoad(index, draftId)
        )(ua)
    case page @ SettlorAddressYesNoPage(index)               =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = SettlorIndividualAddressUKYesNoController.onPageLoad(index, draftId),
          noCall = showOrHideMentalCapacityQuestion(ua, index, draftId)
        )(ua)
    case page @ SettlorIndividualIDCardYesNoPage(index)      =>
      ua =>
        yesNoNav(
          fromPage = page,
          yesCall = SettlorIndividualIDCardController.onPageLoad(index, draftId),
          noCall = showOrHideMentalCapacityQuestion(ua, index, draftId)
        )(ua)
  }

  private def navigateAwayFromCountryOfNationalityQuestions(isTaxable: Boolean, index: Int, draftId: String): Call =
    if (isTaxable) {
      SettlorIndividualNINOYesNoController.onPageLoad(index, draftId)
    } else {
      CountryOfResidencyYesNoController.onPageLoad(index, draftId)
    }

  private def navigateAwayFromCountryOfResidencyQuestions(ua: UserAnswers, index: Int, draftId: String): Call =
    if (isNinoDefined(ua, index) || !ua.isTaxable) {
      showOrHideMentalCapacityQuestion(ua, index, draftId)
    } else {
      SettlorIndividualAddressYesNoController.onPageLoad(index, draftId)
    }

  private def isNinoDefined(ua: UserAnswers, index: Int): Boolean =
    ua.get(SettlorIndividualNINOPage(index)).isDefined

  private def showOrHideMentalCapacityQuestion(ua: UserAnswers, index: Int, draftId: String): Call =
    if (ua.get(SettlorAliveYesNoPage(index)).contains(true)) {
      MentalCapacityYesNoController.onPageLoad(index, draftId)
    } else {
      SettlorIndividualAnswerController.onPageLoad(index, draftId)
    }
}
