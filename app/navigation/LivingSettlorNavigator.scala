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
import controllers.living_settlor.business.{routes => businessRoutes}
import controllers.living_settlor.routes
import javax.inject.{Inject, Singleton}
import models.{NormalMode, UserAnswers}
import models.pages.AddASettlor
import models.pages.IndividualOrBusiness._
import models.pages.KindOfTrust._
import pages.living_settlor._
import pages.living_settlor.business._
import pages.living_settlor.trust_type._
import pages.{AddASettlorPage, AddASettlorYesNoPage, AddAnotherSettlorYesNoPage, SetUpAfterSettlorDiedYesNoPage, _}
import play.api.mvc.Call
import sections.LivingSettlors
import uk.gov.hmrc.auth.core.AffinityGroup

@Singleton
class LivingSettlorNavigator @Inject()(config: FrontendAppConfig) extends Navigator(config) {

  override protected def route(draftId: String): PartialFunction[Page, AffinityGroup => UserAnswers => Call] = {
    case SetUpAfterSettlorDiedYesNoPage  => _ => yesNoNav(SetUpAfterSettlorDiedYesNoPage,
      yesCall = controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId),
      noCall = controllers.living_settlor.routes.KindOfTrustController.onPageLoad(NormalMode, draftId))
    case KindOfTrustPage => _ => kindOfTrustPage(draftId)
    case EfrbsYesNoPage  => _ => yesNoNav(EfrbsYesNoPage,
      yesCall = routes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId),
      noCall = routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId))
    case EfrbsStartDatePage => _ => _ => routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
    case AddAnotherSettlorYesNoPage => _ => _ => controllers.living_settlor.routes.SettlorIndividualAnswerController.onPageLoad(0, draftId)
    case SetUpInAdditionToWillTrustYesNoPage  => _ => yesNoNav(SetUpInAdditionToWillTrustYesNoPage,
      yesCall = controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId),
      noCall = controllers.routes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId))
    case HowDeedOfVariationCreatedPage => _ => _ => controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
    case HoldoverReliefYesNoPage => _ => _ => routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
    case SettlorIndividualNamePage(index) => _ => _ => routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualDateOfBirthYesNoPage(index)  => _ => yesNoNav(SettlorIndividualDateOfBirthYesNoPage(index),
      yesCall = routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId),
      noCall = routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId))
    case SettlorIndividualDateOfBirthPage(index) => _ => _ => routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualNINOYesNoPage(index)  => _ => yesNoNav(SettlorIndividualNINOYesNoPage(index),
      yesCall = routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId),
      noCall = routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId))
    case SettlorIndividualNINOPage(index) => _ => _ => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorAddressYesNoPage(index)  => _ => yesNoNav(SettlorAddressYesNoPage(index),
      yesCall = routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
      noCall = routes.SettlorIndividualAnswerController.onPageLoad(index, draftId))
    case SettlorAddressUKYesNoPage(index)  => _ => yesNoNav(SettlorAddressUKYesNoPage(index),
      yesCall = routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId),
      noCall = routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId))
    case SettlorAddressUKPage(index) => _ => _ => routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorAddressInternationalPage(index) => _ => _ => routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorIndividualPassportYesNoPage(index)  => _ => yesNoNav(SettlorIndividualPassportYesNoPage(index),
      yesCall = routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId),
      noCall = routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId))
    case SettlorIndividualPassportPage(index) => _ => _ => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualIDCardYesNoPage(index)  => _ => yesNoNav(SettlorIndividualIDCardYesNoPage(index),
      yesCall = routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId),
      noCall = routes.SettlorIndividualAnswerController.onPageLoad(index, draftId))
    case SettlorIndividualIDCardPage(index) => _ => _ => routes.SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualOrBusinessPage(index) => _ => settlorIndividualOrBusinessPage(index, draftId)
    case SettlorIndividualAnswerPage => _ => _ => controllers.routes.AddASettlorController.onPageLoad(draftId)
    case SettlorBusinessNamePage(index)  =>_ => _ => businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorBusinessUtrYesNoPage(index)  => _ => yesNoNav(SettlorBusinessUtrYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId),
      noCall = businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId))
    case SettlorBusinessUtrPage(index) => _ => displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessAddressYesNoPage(index) => _ => ua => yesNoNav(SettlorBusinessAddressYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
      noCall = displayAdditionalQuestionsForEmploymentTrusts(draftId, index)(ua))(ua)
    case SettlorBusinessAddressUKYesNoPage(index)  => _ => yesNoNav(SettlorBusinessAddressUKYesNoPage(index),
      yesCall = businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId),
      noCall = businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId))
    case SettlorBusinessAddressUKPage(index) => _ => displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessAddressInternationalPage(index) => _ => displayAdditionalQuestionsForEmploymentTrusts(draftId, index)
    case SettlorBusinessTypePage(index) => _ => _ => businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorBusinessTimeYesNoPage(index) => _ => _ => businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
    case SettlorBusinessAnswerPage => _ => _ => controllers.routes.AddASettlorController.onPageLoad(draftId)
    case AddASettlorPage => _ => addSettlorRoute(draftId)
    case AddASettlorYesNoPage  => _ => yesNoNav(AddASettlorYesNoPage,
      yesCall = routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId),
      noCall = Call("GET", config.registrationProgressUrl(draftId))
    )
  }

  private def addSettlorRoute(draftId: String)(answers: UserAnswers) = {

    def routeToSettlorIndex = {
      val settlors = answers.get(LivingSettlors).getOrElse(List.empty)
      settlors match {
        case Nil =>
          routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
        case t if t.nonEmpty =>
          routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, t.size, draftId)
      }
    }

    answers.get(AddASettlorPage) match {
      case Some(AddASettlor.YesNow) =>
        routeToSettlorIndex
      case Some(AddASettlor.YesLater) =>
        Call("GET", config.registrationProgressUrl(draftId))
      case Some(AddASettlor.NoComplete) =>
        Call("GET", config.registrationProgressUrl(draftId))
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def kindOfTrustPage(draftId: String)(answers: UserAnswers) = {
    answers.get(KindOfTrustPage) match {
      case Some(Deed) =>
        controllers.routes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId)
      case Some(Intervivos) =>
        routes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId)
      case Some(FlatManagement) =>
        routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
      case Some(HeritageMaintenanceFund) =>
        routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, 0, draftId)
      case Some(Employees) =>
        routes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId)
      case _ => controllers.routes.SessionExpiredController.onPageLoad()
    }
  }

  private def displayAdditionalQuestionsForEmploymentTrusts(draftId: String, index: Int)(answers: UserAnswers): Call =
    answers.get(KindOfTrustPage) match {
      case Some(Employees) => businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId)
      case Some(_) => businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def settlorIndividualOrBusinessPage(index: Int, draftId: String)(answers: UserAnswers) =
    answers.get(SettlorIndividualOrBusinessPage(index)) match {
      case Some(Individual) => routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId)
      case Some(Business) =>
          controllers.living_settlor.business.routes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId)
      case None => controllers.routes.SessionExpiredController.onPageLoad()
    }

  private def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call)(answers: UserAnswers): Call = {
    answers.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
