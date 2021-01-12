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

import controllers.living_settlor.routes
import javax.inject.Singleton
import models.pages.KindOfTrust._
import models.{Mode, NormalMode, UserAnswers}
import pages._
import pages.trust_type._
import play.api.mvc.Call
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class TrustTypeNavigator extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String,
                        af: AffinityGroup = AffinityGroup.Organisation,
                        fiveMldEnabled: Boolean = false): UserAnswers => Call = route(draftId, fiveMldEnabled)(page)(af)

  override protected def route(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SetUpAfterSettlorDiedYesNoPage  => _ => yesNoNav(
      fromPage = SetUpAfterSettlorDiedYesNoPage,
      yesCall = controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId),
      noCall = controllers.trust_type.routes.KindOfTrustController.onPageLoad(NormalMode, draftId)
    )
    case KindOfTrustPage => _ => kindOfTrustPage(draftId)
    case EfrbsYesNoPage  => _ => yesNoNav(
      fromPage = EfrbsYesNoPage,
      yesCall = controllers.trust_type.routes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId),
      noCall = routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
    )
    case SetUpInAdditionToWillTrustYesNoPage  => _ => yesNoNav(
      fromPage = SetUpInAdditionToWillTrustYesNoPage,
      yesCall = controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId),
      noCall = controllers.trust_type.routes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId)
    )
    case EfrbsStartDatePage | HowDeedOfVariationCreatedPage | HoldoverReliefYesNoPage => _ => _ =>
      routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
  }

  private def kindOfTrustPage(draftId: String)(answers: UserAnswers): Call = {
    answers.get(KindOfTrustPage) match {
      case Some(Deed) =>
        controllers.trust_type.routes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId)
      case Some(Intervivos) =>
        controllers.trust_type.routes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId)
      case Some(FlatManagement) | Some(HeritageMaintenanceFund) =>
        routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
      case Some(Employees) =>
        controllers.trust_type.routes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }
}
