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

import controllers.living_settlor.business.{routes => businessRoutes}
import javax.inject.Singleton
import models.pages.KindOfTrust._
import models.{Mode, NormalMode, UserAnswers}
import pages._
import pages.living_settlor.business._
import pages.trust_type._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class BusinessSettlorNavigator extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String,
                        af: AffinityGroup = AffinityGroup.Organisation,
                        fiveMldEnabled: Boolean = false): UserAnswers => Call = route(draftId, fiveMldEnabled)(page)(af)

  override protected def route(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorBusinessNamePage(index)  =>_ => _ =>
      businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorBusinessUtrYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorBusinessUtrYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId),
      noCall = businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorBusinessUtrPage(index) => _ =>
      displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessAddressYesNoPage(index) => _ => ua => yesNoNav(
      fromPage = SettlorBusinessAddressYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
      noCall = displayAdditionalQuestionsForEmploymentTrusts(draftId, index)(ua)
    )(ua)
    case SettlorBusinessAddressUKYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorBusinessAddressUKYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId),
      noCall = businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorBusinessAddressUKPage(index) => _ =>
      displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessAddressInternationalPage(index) => _ =>
      displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessTypePage(index) => _ => _ =>
      businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorBusinessTimeYesNoPage(index) => _ => _ =>
      businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
    case SettlorBusinessAnswerPage => _ => _ =>
      controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def displayAdditionalQuestionsForEmploymentTrusts(draftId: String, index: Int)(answers: UserAnswers): Call =
    answers.get(KindOfTrustPage) match {
      case Some(Employees) => businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId)
      case Some(_) => businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
}
