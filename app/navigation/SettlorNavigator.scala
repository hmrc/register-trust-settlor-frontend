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
import models.UserAnswers
import models.pages.AddASettlor._
import models.pages.IndividualOrBusiness
import models.pages.IndividualOrBusiness.{Business, Individual}
import models.pages.KindOfTrust._
import pages.living_settlor._
import pages.{AddASettlorPage, AddASettlorYesNoPage, _}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.Constants.MAX
import viewmodels.SettlorViewModel

import javax.inject.{Inject, Singleton}

@Singleton
class SettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator {

  override def nextPage(page: Page,
                        draftId: String): UserAnswers => Call = route(draftId)(page)

  override protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case AddASettlorPage => addSettlorRoute(draftId)
    case AddASettlorYesNoPage => yesNoNav(
      fromPage = AddASettlorYesNoPage,
      yesCall = controllers.trust_type.routes.SetUpAfterSettlorDiedController.onPageLoad(draftId),
      noCall = settlorsCompletedRoute(draftId)
    )
    case SettlorIndividualOrBusinessPage(index) => settlorIndividualOrBusinessPage(index, draftId)
  }

  private def settlorsCompletedRoute(draftId: String): Call = {
    Call(GET, config.registrationProgressUrl(draftId))
  }

  private def addSettlorRoute(draftId: String)(answers: UserAnswers): Call = {

    answers.get(AddASettlorPage) match {
      case Some(YesNow) => SettlorNavigator.addSettlorRoute(answers, draftId)
      case Some(_) => settlorsCompletedRoute(draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def settlorIndividualOrBusinessPage(index: Int, draftId: String)(answers: UserAnswers): Call =
    answers.get(SettlorIndividualOrBusinessPage(index)) match {
      case Some(individualOrBusiness) => SettlorNavigator.addSettlorNowRoute(individualOrBusiness, answers, draftId, Some(index))
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
}

object SettlorNavigator {

  def addSettlorRoute(answers: UserAnswers, draftId: String): Call = {
    val routes: List[(List[SettlorViewModel], Call)] = List(
      (answers.settlors.individuals, addSettlorNowRoute(Individual, answers, draftId)),
      (answers.settlors.businesses, addSettlorNowRoute(Business, answers, draftId))
    )

    routes.filter(_._1.size < MAX) match {
      case (_, x) :: Nil => x
      case _ => routeToIndex(answers, controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad, draftId)
    }
  }

  def addSettlorNowRoute(`type`: IndividualOrBusiness, answers: UserAnswers, draftId: String, index: Option[Int] = None): Call = {
    `type` match {
      case Individual => routeToIndividualSettlorIndex(answers, draftId, index)
      case Business => routeToSettlorProtectorIndex(answers, draftId, index)
    }
  }

  private def routeToIndividualSettlorIndex(answers: UserAnswers, draftId: String, index: Option[Int]): Call = {
    routeToIndex(answers, controllers.living_settlor.individual.routes.SettlorIndividualNameController.onPageLoad, draftId, index)
  }

  private def routeToSettlorProtectorIndex(answers: UserAnswers, draftId: String, index: Option[Int]): Call = {
    routeToIndex(answers, controllers.living_settlor.business.routes.SettlorBusinessNameController.onPageLoad, draftId, index)
  }

  private def routeToIndex[T <: SettlorViewModel](answers: UserAnswers,
                                                  route: (Int, String) => Call,
                                                  draftId: String,
                                                  index: Option[Int] = None): Call = {
    val i = index.getOrElse {
      answers.get(sections.LivingSettlors).getOrElse(Nil).size
    }
    route(i, draftId)
  }
}
