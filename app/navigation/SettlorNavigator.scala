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
import controllers.living_settlor.routes
import javax.inject.{Inject, Singleton}
import models.pages.AddASettlor._
import models.pages.IndividualOrBusiness.{Business, Individual}
import models.pages.KindOfTrust._
import models.{Mode, NormalMode, UserAnswers}
import pages.living_settlor._
import pages.{AddASettlorPage, AddASettlorYesNoPage, _}
import play.api.mvc.Call
import sections.LivingSettlors
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class SettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page, mode: Mode, draftId: String,
                        af: AffinityGroup = AffinityGroup.Organisation,
                        is5mldEnabled: Boolean = false): UserAnswers => Call = route(draftId, is5mldEnabled)(page)(af)

  override protected def route(draftId: String, fiveMld: Boolean): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case AddASettlorPage => _ => addSettlorRoute(draftId)
    case AddASettlorYesNoPage  => _ => yesNoNav(AddASettlorYesNoPage,
      yesCall = routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId),
      noCall = Call("GET", config.registrationProgressUrl(draftId))
    )
    case SettlorIndividualOrBusinessPage(index) => _ => settlorIndividualOrBusinessPage(index, draftId)
  }

  private def addSettlorRoute(draftId: String)(answers: UserAnswers): Call = {

    def routeToSettlorIndex: Call = {
      val settlors = answers.get(LivingSettlors).getOrElse(List.empty)
      routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, settlors.size, draftId)
    }

    answers.get(AddASettlorPage) match {
      case Some(YesNow) =>
        routeToSettlorIndex
      case Some(YesLater) | Some(NoComplete) =>
        Call("GET", config.registrationProgressUrl(draftId))
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def settlorIndividualOrBusinessPage(index: Int, draftId: String)(answers: UserAnswers): Call =
    answers.get(SettlorIndividualOrBusinessPage(index)) match {
      case Some(Individual) => controllers.living_settlor.individual.routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId)
      case Some(Business) => controllers.living_settlor.business.routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }
}
