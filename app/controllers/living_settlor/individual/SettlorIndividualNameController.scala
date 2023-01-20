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

package controllers.living_settlor.individual

import config.annotations.IndividualSettlor
import controllers.actions.Actions
import forms.living_settlor.SettlorIndividualNameFormProvider
import models.pages.FullName
import navigation.Navigator
import pages.living_settlor.individual.SettlorIndividualNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.living_settlor.individual.SettlorIndividualNameView
import javax.inject.Inject
import play.api.Logging

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SettlorIndividualNameController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 registrationsRepository: RegistrationsRepository,
                                                 @IndividualSettlor navigator: Navigator,
                                                 actions: Actions,
                                                 formProvider: SettlorIndividualNameFormProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: SettlorIndividualNameView
                                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[FullName] = formProvider()

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorIndividualNamePage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index))),

        value => {
          request.userAnswers.set(SettlorIndividualNamePage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(SettlorIndividualNamePage(index), draftId)(updatedAnswers))
              }
            case Failure(_) => {
              logger.error("[SettlorIndividualNameController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError("ok"))
            }
          }
        }
      )
  }
}
