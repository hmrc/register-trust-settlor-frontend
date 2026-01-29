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

package controllers

import connectors.SubmissionDraftConnector
import controllers.actions.RegistrationIdentifierAction
import models.TaskStatus.InProgress
import models.UserAnswers
import models.pages.Status.Completed
import models.requests.IdentifierRequest
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import sections.{DeceasedSettlor, LivingSettlors}
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  repository: RegistrationsRepository,
  identify: RegistrationIdentifierAction,
  trustsStoreService: TrustsStoreService,
  submissionDraftConnector: SubmissionDraftConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = identify.async { implicit request =>
    def redirect(userAnswers: UserAnswers)(implicit request: IdentifierRequest[AnyContent]): Future[Result] =
      repository.set(userAnswers) map { _ =>
        val livingSettlors  = userAnswers.get(LivingSettlors).getOrElse(List.empty)
        val deceasedSettlor = userAnswers.get(DeceasedSettlor)

        if (livingSettlors.nonEmpty) {
          Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId))
        } else if (deceasedSettlor.map(_.status).contains(Completed)) {
          Redirect(controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(draftId))
        } else {
          Redirect(controllers.routes.SettlorInfoController.onPageLoad(draftId))
        }
      }

    for {
      isTaxable   <- submissionDraftConnector.getIsTrustTaxable(draftId)
      utr         <- submissionDraftConnector.getTrustUtr(draftId)
      userAnswers <- repository.get(draftId)
      ua           = userAnswers match {
                       case Some(value) => value.copy(isTaxable = isTaxable, existingTrustUtr = utr)
                       case None        => UserAnswers(draftId, Json.obj(), request.internalId, isTaxable, utr)
                     }
      result      <- redirect(ua)
      _           <- trustsStoreService.updateTaskStatus(draftId, InProgress)
    } yield result
  }

}
