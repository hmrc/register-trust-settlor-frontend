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
import forms.YesNoFormProvider
import navigation.Navigator
import pages.trust_type.{HoldoverReliefYesNoPage, SetUpByLivingSettlorYesNoPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.trust_type.HoldoverReliefYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class HoldoverReliefYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @TrustType navigator: Navigator,
  standardActions: Actions,
  requiredAnswer: RequiredAnswerActionProvider,
  yesNoFormProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: HoldoverReliefYesNoView,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("holdoverReliefYesNo")

  private def actions(draftId: String) =
    standardActions.authWithData(draftId) andThen
      requiredAnswer(
        RequiredAnswer(SetUpByLivingSettlorYesNoPage, routes.SetUpByLivingSettlorController.onPageLoad(draftId))
      )

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(HoldoverReliefYesNoPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions(draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[Boolean]) => Future.successful(BadRequest(view(formWithErrors, draftId))),
        value =>
          request.userAnswers.set(HoldoverReliefYesNoPage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(HoldoverReliefYesNoPage, draftId)(updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[HoldoverReliefYesNoController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
          }
      )
  }

}
