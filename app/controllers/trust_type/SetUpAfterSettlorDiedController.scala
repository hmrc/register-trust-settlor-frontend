/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.YesNoFormProvider
import navigation.Navigator
import pages.trust_type.SetUpAfterSettlorDiedYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.trust_type.SetUpAfterSettlorDiedView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SetUpAfterSettlorDiedController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 registrationsRepository: RegistrationsRepository,
                                                 @TrustType navigator: Navigator,
                                                 actions: Actions,
                                                 yesNoFormProvider: YesNoFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: SetUpAfterSettlorDiedView,
                                                 technicalErrorView: TechnicalErrorView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("setUpAfterSettlorDiedYesNo")

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SetUpAfterSettlorDiedYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, request.userAnswers.isTaxable))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, request.userAnswers.isTaxable))),
        value => {
          request.userAnswers.set(SetUpAfterSettlorDiedYesNoPage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, draftId)(updatedAnswers))
              }
            case Failure(_) => {
              logger.error("[SetUpAfterSettlorDiedController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
            }
          }
        }
      )
  }
}
