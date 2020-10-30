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
import controllers.routes
import javax.inject.{Inject, Singleton}
import models.{NormalMode, UserAnswers}
import pages.{AddAnotherSettlorYesNoPage, Page}
import pages.deceased_settlor._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class DeceasedSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override protected def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case AddAnotherSettlorYesNoPage => _ => _ => controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsNamePage => _ => _ => controllers.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorDateOfDeathYesNoPage => _ => deceasedSettlorDateOfDeathRoute(draftId)
    case SettlorDateOfBirthYesNoPage => _ => deceasedSettlorDateOfBirthRoute(draftId)
    case SettlorsDateOfBirthPage => _ => _ => controllers.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorsNationalInsuranceYesNoPage => _ => deceasedSettlorNinoRoute(draftId)
    case SettlorsLastKnownAddressYesNoPage => _ => deceasedSettlorLastKnownAddressRoute(draftId)
    case SettlorDateOfDeathPage => _ => _ => controllers.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case SettlorNationalInsuranceNumberPage => _ => _ => controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case WasSettlorsAddressUKYesNoPage => _ => deceasedSettlorAddressRoute(draftId)
    case SettlorsInternationalAddressPage => _ => _ => controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case SettlorsUKAddressPage => _ => _ => controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case DeceasedSettlorAnswerPage => _ => _ => Call("GET", config.registrationProgressUrl(draftId))
  }

  private def deceasedSettlorAddressRoute(draftId: String)(userAnswers: UserAnswers): Call = userAnswers.get(WasSettlorsAddressUKYesNoPage) match {
    case Some(false) => controllers.deceased_settlor.routes.SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.deceased_settlor.routes.SettlorsUKAddressController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorLastKnownAddressRoute(draftId: String)(userAnswers: UserAnswers): Call = userAnswers.get(SettlorsLastKnownAddressYesNoPage) match {
    case Some(false) => controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId)
    case Some(true) => controllers.deceased_settlor.routes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorNinoRoute(draftId: String)(userAnswers: UserAnswers): Call = userAnswers.get(SettlorsNationalInsuranceYesNoPage) match {
    case Some(false) => controllers.deceased_settlor.routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.deceased_settlor.routes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorDateOfBirthRoute(draftId: String)(userAnswers: UserAnswers): Call = userAnswers.get(SettlorDateOfBirthYesNoPage) match {
    case Some(false) => controllers.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.deceased_settlor.routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def deceasedSettlorDateOfDeathRoute(draftId: String)(userAnswers: UserAnswers): Call = userAnswers.get(SettlorDateOfDeathYesNoPage) match {
    case Some(false) => controllers.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId)
    case Some(true) => controllers.deceased_settlor.routes.SettlorDateOfDeathController.onPageLoad(NormalMode, draftId)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

}
