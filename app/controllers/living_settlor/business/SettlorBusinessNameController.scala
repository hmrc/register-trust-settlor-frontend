/*
 * Copyright 2026 HM Revenue & Customs
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

package controllers.living_settlor.business

import config.annotations.BusinessSettlor
import controllers.actions.Actions
import forms.living_settlor.SettlorBusinessNameFormProvider
import navigation.Navigator
import pages.living_settlor.business.SettlorBusinessNamePage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.living_settlor.business.SettlorBusinessNameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SettlorBusinessNameController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @BusinessSettlor navigator: Navigator,
  actions: Actions,
  formProvider: SettlorBusinessNameFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SettlorBusinessNameView,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[String] = formProvider()

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions.authWithData(draftId) { implicit request =>
    val preparedForm = request.userAnswers.get(SettlorBusinessNamePage(index)) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, index, draftId))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    actions.authWithData(draftId).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[String]) => Future.successful(BadRequest(view(formWithErrors, index, draftId))),
          value =>
            request.userAnswers.set(SettlorBusinessNamePage(index), value) match {
              case Success(updatedAnswers) =>
                registrationsRepository.set(updatedAnswers).map { _ =>
                  Redirect(navigator.nextPage(SettlorBusinessNamePage(index), draftId)(updatedAnswers))
                }
              case Failure(_)              =>
                logger.error("[SettlorBusinessNameController][onSubmit] Error while storing user answers")
                Future.successful(InternalServerError(technicalErrorView()))
            }
        )
    }

}
