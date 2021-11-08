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

package controllers.living_settlor.individual

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.actions.living_settlor.individual.NameRequiredActionProvider
import models.pages.KindOfTrust.Employees
import models.pages.Status.Completed
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.LivingSettlorStatus
import pages.living_settlor.individual.SettlorIndividualAnswerPage
import pages.trust_type.KindOfTrustPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.DraftRegistrationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.LivingSettlorPrintHelper
import views.html.living_settlor.SettlorAnswersView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualAnswerController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   registrationsRepository: RegistrationsRepository,
                                                   draftRegistrationService: DraftRegistrationService,
                                                   @IndividualSettlor navigator: Navigator,
                                                   actions: Actions,
                                                   requireName: NameRequiredActionProvider,
                                                   view: SettlorAnswersView,
                                                   val controllerComponents: MessagesControllerComponents,
                                                   livingSettlorPrintHelper: LivingSettlorPrintHelper
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String): ActionBuilder[SettlorIndividualNameRequest, AnyContent] =
    actions.authWithData(draftId) andThen requireName(index, draftId)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val section = livingSettlorPrintHelper.checkDetailsSection(request.userAnswers, request.name.toString, draftId, index)

      Ok(view(routes.SettlorIndividualAnswerController.onSubmit(index, draftId), Seq(section)))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(LivingSettlorStatus(index), Completed))
        _ <- registrationsRepository.set(updatedAnswers)
        _ <- draftRegistrationService.amendBeneficiariesState(draftId, updatedAnswers)
        _ <- draftRegistrationService.removeDeceasedSettlorMappedPiece(draftId)
      } yield Redirect(navigator.nextPage(SettlorIndividualAnswerPage, draftId)(updatedAnswers))
  }

}
