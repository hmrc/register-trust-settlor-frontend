/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import models.{NormalMode, UserAnswers}
import pages.Page
import pages.deceased_settlor._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class DeceasedSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override protected def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorsNamePage => _ => _ =>
      controllers.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorDateOfDeathYesNoPage => _ => yesNoNav(
      fromPage = SettlorDateOfDeathYesNoPage,
      yesCall = SettlorDateOfDeathController.onPageLoad(NormalMode, draftId),
      noCall = SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    )
    case SettlorDateOfDeathPage => _ => _ =>
      controllers.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorDateOfBirthYesNoPage => _ => yesNoNav(
      fromPage = SettlorDateOfBirthYesNoPage,
      yesCall = SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    )
    case SettlorsDateOfBirthPage => _ => _ =>
      controllers.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorsNationalInsuranceYesNoPage => _ => yesNoNav(
      fromPage = SettlorsNationalInsuranceYesNoPage,
      yesCall = SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId),
      noCall = SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId)
    )
    case SettlorNationalInsuranceNumberPage => _ => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
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
    case SettlorsUKAddressPage => _ => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsInternationalAddressPage => _ => _ =>
      controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case DeceasedSettlorAnswerPage => _ => _ =>
      Call("GET", config.registrationProgressUrl(draftId))
  }

}
