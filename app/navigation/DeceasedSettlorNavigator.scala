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

import config.FrontendAppConfig
import controllers.deceased_settlor.mld5.{routes => mld5Rts}
import controllers.deceased_settlor.{routes => rts}
import models.UserAnswers
import pages.Page
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET

import javax.inject.{Inject, Singleton}

@Singleton
class DeceasedSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page,
                        draftId: String): UserAnswers => Call = route(draftId)(page)

  override protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId) orElse
      mldDependentSimpleNavigation(draftId) orElse
      mldDependentYesNoNavigation(draftId)
  }

  def simpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorsNamePage => _ =>
      rts.SettlorDateOfDeathYesNoController.onPageLoad(draftId)
    case SettlorDateOfDeathPage => _ =>
      rts.SettlorDateOfBirthYesNoController.onPageLoad(draftId)
    case CountryOfResidencePage => ua =>
      navigateAwayFromCountryOfResidencyQuestions(draftId)(ua)
    case SettlorsUKAddressPage => _ =>
      rts.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsInternationalAddressPage => _ =>
      rts.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case CountryOfNationalityPage => ua =>
      navigateAwayFromNationalityQuestions(ua.isTaxable, draftId)
    case DeceasedSettlorAnswerPage => _ =>
      Call(GET, config.registrationProgressUrl(draftId))
  }

  def yesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorDateOfDeathYesNoPage => yesNoNav(
      fromPage = SettlorDateOfDeathYesNoPage,
      yesCall = rts.SettlorDateOfDeathController.onPageLoad(draftId),
      noCall = rts.SettlorDateOfBirthYesNoController.onPageLoad(draftId)
    )
    case CountryOfResidenceYesNoPage => answers => yesNoNav(
      fromPage = CountryOfResidenceYesNoPage,
      yesCall = mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(draftId),
      noCall = navigateAwayFromCountryOfResidencyQuestions(draftId)(answers)
    )(answers)
    case CountryOfResidenceInTheUkYesNoPage => answers => yesNoNav(
      fromPage = CountryOfResidenceInTheUkYesNoPage,
      yesCall = navigateAwayFromCountryOfResidencyQuestions(draftId)(answers),
      noCall = mld5Rts.CountryOfResidenceController.onPageLoad(draftId)
    )(answers)
    case SettlorsLastKnownAddressYesNoPage => yesNoNav(
      fromPage = SettlorsLastKnownAddressYesNoPage,
      yesCall = rts.WasSettlorsAddressUKYesNoController.onPageLoad(draftId),
      noCall = rts.DeceasedSettlorAnswerController.onPageLoad(draftId)
    )
    case WasSettlorsAddressUKYesNoPage => yesNoNav(
      fromPage = WasSettlorsAddressUKYesNoPage,
      yesCall = rts.SettlorsUKAddressController.onPageLoad(draftId),
      noCall = rts.SettlorsInternationalAddressController.onPageLoad(draftId)
    )
    case CountryOfNationalityYesNoPage => answers => yesNoNav(
      fromPage = CountryOfNationalityYesNoPage,
      yesCall = mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(draftId),
      noCall = navigateAwayFromNationalityQuestions(answers.isTaxable, draftId)
    )(answers)
    case CountryOfNationalityInTheUkYesNoPage => answers => yesNoNav(
      fromPage = CountryOfNationalityInTheUkYesNoPage,
      yesCall = navigateAwayFromNationalityQuestions(answers.isTaxable, draftId),
      noCall = mld5Rts.CountryOfNationalityController.onPageLoad(draftId)
    )(answers)
  }

  private def mldDependentSimpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorsDateOfBirthPage => ua =>
      mld5Rts.CountryOfNationalityYesNoController.onPageLoad(draftId)
    case SettlorNationalInsuranceNumberPage => _ =>
        mld5Rts.CountryOfResidenceYesNoController.onPageLoad(draftId)
  }

  private def mldDependentYesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorDateOfBirthYesNoPage => ua => yesNoNav(
      fromPage = SettlorDateOfBirthYesNoPage,
      yesCall = rts.SettlorsDateOfBirthController.onPageLoad(draftId),
      noCall = mld5Rts.CountryOfNationalityYesNoController.onPageLoad(draftId)
    )(ua)
    case SettlorsNationalInsuranceYesNoPage => ua => yesNoNav(
      fromPage = SettlorsNationalInsuranceYesNoPage,
      yesCall = rts.SettlorNationalInsuranceNumberController.onPageLoad(draftId),
      noCall = mld5Rts.CountryOfResidenceYesNoController.onPageLoad(draftId)
    )(ua)
  }

  private def navigateAwayFromNationalityQuestions(isTaxable: Boolean, draftId: String): Call = {
    if (isTaxable) {
      rts.SettlorsNINoYesNoController.onPageLoad(draftId)
    } else {
      mld5Rts.CountryOfResidenceYesNoController.onPageLoad(draftId)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(draftId: String)(answers: UserAnswers): Call = {
    answers.get(SettlorsNationalInsuranceYesNoPage) match {
      case Some(false) if answers.isTaxable =>
        rts.SettlorsLastKnownAddressYesNoController.onPageLoad(draftId)
      case _ =>
        rts.DeceasedSettlorAnswerController.onPageLoad(draftId)
    }
  }

}
