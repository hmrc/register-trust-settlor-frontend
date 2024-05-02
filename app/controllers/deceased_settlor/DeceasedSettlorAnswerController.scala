/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.deceased_settlor

import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import models.TaskStatus
import models.pages.Status.Completed
import navigation.Navigator
import pages.DeceasedSettlorStatus
import pages.deceased_settlor.DeceasedSettlorAnswerPage
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.{DraftRegistrationService, TrustsStoreService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.print.DeceasedSettlorPrintHelper
import views.html.deceased_settlor.DeceasedSettlorAnswerView
import views.html.errors.TechnicalErrorView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class DeceasedSettlorAnswerController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @DeceasedSettlor navigator: Navigator,
  actions: Actions,
  requireName: NameRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DeceasedSettlorAnswerView,
  draftRegistrationService: DraftRegistrationService,
  deceasedSettlorPrintHelper: DeceasedSettlorPrintHelper,
  trustsStoreService: TrustsStoreService,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)) {
    implicit request =>
      val section = deceasedSettlorPrintHelper.checkDetailsSection(request.userAnswers, request.name.toString, draftId)

      Ok(view(draftId, Seq(section)))
  }

  def onSubmit(draftId: String): Action[AnyContent] =
    (actions.authWithData(draftId) andThen requireName(draftId)).async { implicit request =>
      request.userAnswers.set(DeceasedSettlorStatus, Completed) match {
        case Success(updatedAnswers) =>
          registrationsRepository.set(updatedAnswers).map { _ =>
            draftRegistrationService.removeLivingSettlorsMappedPiece(draftId)
            trustsStoreService.updateTaskStatus(draftId, TaskStatus.Completed)
            Redirect(navigator.nextPage(DeceasedSettlorAnswerPage, draftId)(request.userAnswers))
          }
        case Failure(_)              =>
          logger.error("[DeceasedSettlorAnswerController][onSubmit] Error while storing user answers")
          Future.successful(InternalServerError(technicalErrorView()))
      }
    }
}
