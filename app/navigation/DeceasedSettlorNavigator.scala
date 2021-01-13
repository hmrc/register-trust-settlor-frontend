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
import controllers.deceased_settlor.routes._
import controllers.deceased_settlor.nonTaxable.routes._
import javax.inject.{Inject, Singleton}
import models.{Mode, NormalMode, UserAnswers}
import pages.Page
import pages.deceased_settlor._
import pages.deceased_settlor.nonTaxable._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class DeceasedSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String,
                        af: AffinityGroup = AffinityGroup.Organisation,
                        fiveMldEnabled: Boolean = false): UserAnswers => Call = route(draftId, fiveMldEnabled)(page)(af)

  def simpleNavigation(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorsNamePage => _ => _ =>
      controllers.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorDateOfDeathPage => _ => _ =>
      controllers.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorsDateOfBirthPage => _ => _ => fiveMldYesNo(draftId, fiveMld)
    case SettlorNationalInsuranceNumberPage => _ => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsUKAddressPage => _ => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsInternationalAddressPage => _ => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case CountryOfNationalityPage => _ => _ =>
      controllers.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case DeceasedSettlorAnswerPage => _ => _ =>
      Call("GET", config.registrationProgressUrl(draftId))
  }

  def yesNoNavigation(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorDateOfDeathYesNoPage => _ => yesNoNav(
      fromPage = SettlorDateOfDeathYesNoPage,
      yesCall = SettlorDateOfDeathController.onPageLoad(NormalMode, draftId),
      noCall = SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    )
    case SettlorDateOfBirthYesNoPage => _ => yesNoNav(
      fromPage = SettlorDateOfBirthYesNoPage,
      yesCall = SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId),
      noCall = fiveMldYesNo(draftId, fiveMld)
    )
    case SettlorsNationalInsuranceYesNoPage => _ => yesNoNav(
      fromPage = SettlorsNationalInsuranceYesNoPage,
      yesCall = SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId)
    )
    case SettlorsLastKnownAddressYesNoPage => _ => yesNoNav(
      fromPage = SettlorsLastKnownAddressYesNoPage,
      yesCall = WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId),
      noCall = DeceasedSettlorAnswerController.onPageLoad(draftId)
    )
    case WasSettlorsAddressUKYesNoPage => _ => yesNoNav(
      fromPage = WasSettlorsAddressUKYesNoPage,
      yesCall = SettlorsUKAddressController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId)
    )
    case CountryOfNationalityYesNoPage => _ => yesNoNav(
      fromPage = CountryOfNationalityYesNoPage,
      yesCall = CountryOfNationalityInTheUkYesNoController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    )
    case CountryOfNationalityInTheUkYesNoPage => _ => yesNoNav(
      fromPage = CountryOfNationalityInTheUkYesNoPage,
      yesCall = SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId),
      noCall = CountryOfNationalityController.onPageLoad(NormalMode, draftId)
    )
  }

  private def fiveMldYesNo(draftId: String, fiveMld: Boolean): Call = {
    if (fiveMld) {
      CountryOfNationalityYesNoController.onPageLoad(NormalMode, draftId)
    } else {
      SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    }
  }

  override protected def route(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    simpleNavigation(draftId, fiveMld) orElse yesNoNavigation(draftId, fiveMld)
  }

}
