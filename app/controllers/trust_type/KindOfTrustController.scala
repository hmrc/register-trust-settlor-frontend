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

package controllers.trust_type

import config.annotations.TrustType
import controllers.actions._
import forms.KindOfTrustFormProvider
import models.Enumerable
import models.pages.KindOfTrust
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.trust_type.{KindOfTrustPage, SetUpByLivingSettlorYesNoPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.trust_type.KindOfTrustView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class KindOfTrustController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @TrustType navigator: Navigator,
  standardActions: Actions,
  formProvider: KindOfTrustFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: KindOfTrustView,
  requiredAnswer: RequiredAnswerActionProvider,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Enumerable.Implicits
    with Logging {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActions.authWithData(draftId) andThen
      requiredAnswer(
        RequiredAnswer(SetUpByLivingSettlorYesNoPage, routes.SetUpByLivingSettlorController.onPageLoad(draftId))
      )

  private val form: Form[KindOfTrust] = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(KindOfTrustPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[KindOfTrust]) => Future.successful(BadRequest(view(formWithErrors, draftId))),
        value =>
          request.userAnswers.set(KindOfTrustPage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(KindOfTrustPage, draftId)(updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[KindOfTrustController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
          }
      )
  }
}
