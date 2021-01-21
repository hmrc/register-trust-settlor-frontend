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

import config.FrontendAppConfig
import controllers.deceased_settlor.mld5.routes._
import controllers.deceased_settlor.routes._
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._
import play.api.mvc.Call

import javax.inject.{Inject, Singleton}

@Singleton
class DeceasedSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page,
                        mode: Mode,
                        draftId: String,
                        is5mldEnabled: Boolean = false): UserAnswers => Call = {
    
    route(draftId, is5mldEnabled)(page)
  }

  def simpleNavigation(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorsNamePage => _ =>
      controllers.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorDateOfDeathPage => _ =>
      controllers.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorsDateOfBirthPage => _ => fiveMldNationalityYesNo(draftId, is5mldEnabled)
    case SettlorNationalInsuranceNumberPage => _ => fiveMldResidenceWithNino(draftId, is5mldEnabled)
    case SettlorsUKAddressPage => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsInternationalAddressPage => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case CountryOfNationalityPage => _ =>
      controllers.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case CountryOfResidencePage => answers => fiveMldResidenceCheckNino(draftId)(answers)
    case DeceasedSettlorAnswerPage => _ =>
      Call("GET", config.registrationProgressUrl(draftId))
  }

  def yesNoNavigation(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorDateOfDeathYesNoPage => yesNoNav(
      fromPage = SettlorDateOfDeathYesNoPage,
      yesCall = SettlorDateOfDeathController.onPageLoad(NormalMode, draftId),
      noCall = SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    )
    case SettlorDateOfBirthYesNoPage => yesNoNav(
      fromPage = SettlorDateOfBirthYesNoPage,
      yesCall = SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId),
      noCall = fiveMldNationalityYesNo(draftId, is5mldEnabled)
    )
    case SettlorsNationalInsuranceYesNoPage => yesNoNav(
      fromPage = SettlorsNationalInsuranceYesNoPage,
      yesCall = SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId),
      noCall = fiveMldResidenceYesNo(draftId, is5mldEnabled)
    )
    case SettlorsLastKnownAddressYesNoPage => yesNoNav(
      fromPage = SettlorsLastKnownAddressYesNoPage,
      yesCall = WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId),
      noCall = DeceasedSettlorAnswerController.onPageLoad(draftId)
    )
    case WasSettlorsAddressUKYesNoPage => yesNoNav(
      fromPage = WasSettlorsAddressUKYesNoPage,
      yesCall = SettlorsUKAddressController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId)
    )
    case CountryOfNationalityYesNoPage => yesNoNav(
      fromPage = CountryOfNationalityYesNoPage,
      yesCall = CountryOfNationalityInTheUkYesNoController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    )
    case CountryOfNationalityInTheUkYesNoPage => yesNoNav(
      fromPage = CountryOfNationalityInTheUkYesNoPage,
      yesCall = SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId),
      noCall = CountryOfNationalityController.onPageLoad(NormalMode, draftId)
    )
    case CountryOfResidenceYesNoPage => answers => yesNoNav(
      fromPage = CountryOfResidenceYesNoPage,
      yesCall = CountryOfResidenceInTheUkYesNoController.onPageLoad(NormalMode, draftId),
      noCall = fiveMldResidenceCheckNino(draftId)(answers)
    )(answers)
    case CountryOfResidenceInTheUkYesNoPage => answers => yesNoNav(
      fromPage = CountryOfResidenceInTheUkYesNoPage,
      yesCall = fiveMldResidenceCheckNino(draftId)(answers),
      noCall = CountryOfResidenceController.onPageLoad(NormalMode, draftId)
    )(answers)
  }

  private def fiveMldNationalityYesNo(draftId: String, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfNationalityYesNoController.onPageLoad(NormalMode, draftId)
    } else {
      SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    }
  }

  private def fiveMldResidenceYesNo(draftId: String, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfResidenceYesNoController.onPageLoad(NormalMode, draftId)
    } else {
      SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId)
    }
  }

  private def fiveMldResidenceWithNino(draftId: String, is5mldEnabled: Boolean): Call = {
    if (is5mldEnabled) {
      CountryOfResidenceYesNoController.onPageLoad(NormalMode, draftId)
    } else {
      DeceasedSettlorAnswerController.onPageLoad(draftId)
    }
  }

  private def fiveMldResidenceCheckNino(draftId: String)(answers: UserAnswers): Call = {
    answers.get(SettlorsNationalInsuranceYesNoPage) match {
      case Some(true) => DeceasedSettlorAnswerController.onPageLoad(draftId)
      case _ => SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId)
    }
  }

  override protected def route(draftId: String, is5mldEnabled: Boolean): PartialFunction[Page, UserAnswers => Call] = {
    simpleNavigation(draftId, is5mldEnabled) orElse yesNoNavigation(draftId, is5mldEnabled)
  }

}
