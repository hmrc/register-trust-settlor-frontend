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

import javax.inject.Singleton
import models.{Mode, NormalMode, UserAnswers}
import pages._
import pages.living_settlor.individual._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class IndividualSettlorNavigator extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String,
                        af: AffinityGroup = AffinityGroup.Organisation,
                        is5mldEnabled: Boolean = false): UserAnswers => Call = route(draftId, is5mldEnabled)(page)(af)

  override protected def route(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SettlorIndividualNamePage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualDateOfBirthYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorIndividualDateOfBirthYesNoPage(index),
      yesCall = controllers.living_settlor.individual.routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId),
      noCall = controllers.living_settlor.individual.routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorIndividualDateOfBirthPage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualNINOYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorIndividualNINOYesNoPage(index),
      yesCall = controllers.living_settlor.individual.routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId),
      noCall = controllers.living_settlor.individual.routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorIndividualNINOPage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorAddressYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorAddressYesNoPage(index),
      yesCall = controllers.living_settlor.individual.routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
      noCall = controllers.living_settlor.individual.routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    )
    case SettlorAddressUKYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorAddressUKYesNoPage(index),
      yesCall = controllers.living_settlor.individual.routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId),
      noCall = controllers.living_settlor.individual.routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorAddressUKPage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorAddressInternationalPage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualPassportYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorIndividualPassportYesNoPage(index),
      yesCall = controllers.living_settlor.individual.routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId),
      noCall = controllers.living_settlor.individual.routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId)
    )
    case SettlorIndividualPassportPage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualIDCardYesNoPage(index)  => _ => yesNoNav(
      fromPage = SettlorIndividualIDCardYesNoPage(index),
      yesCall = controllers.living_settlor.individual.routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId),
      noCall = controllers.living_settlor.individual.routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    )
    case SettlorIndividualIDCardPage(index) => _ => _ =>
      controllers.living_settlor.individual.routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualAnswerPage => _ => _ =>
      controllers.routes.AddASettlorController.onPageLoad(draftId)
  }
}
