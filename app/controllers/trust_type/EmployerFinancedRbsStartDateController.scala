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
import controllers.actions.Actions
import forms.EfrbsStartDateFormProvider
import navigation.Navigator
import pages.trust_type.EfrbsStartDatePage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.trust_type.EmployerFinancedRbsStartDateView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class EmployerFinancedRbsStartDateController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @TrustType navigator: Navigator,
  actions: Actions,
  formProvider: EfrbsStartDateFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EmployerFinancedRbsStartDateView,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private val form: Form[LocalDate] = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(EfrbsStartDatePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions.authWithData(draftId).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[LocalDate]) => Future.successful(BadRequest(view(formWithErrors, draftId))),
        value =>
          request.userAnswers.set(EfrbsStartDatePage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(EfrbsStartDatePage, draftId)(updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[EmployerFinancedRbsStartDateController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
          }
      )
  }
}
